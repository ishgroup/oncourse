package ish.oncourse.webservices.replication.services;

import ish.common.types.PaymentStatus;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.services.enrol.IEnrolmentService;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.preference.PreferenceControllerFactory;
import ish.oncourse.utils.SessionIdGenerator;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.replication.builders.ITransactionStubBuilder;
import ish.oncourse.webservices.replication.builders.IWillowStubBuilder;
import ish.oncourse.webservices.soap.v4.FaultCode;
import ish.oncourse.webservices.soap.v4.PaymentPortType;
import ish.oncourse.webservices.soap.v4.ReplicationFault;
import ish.oncourse.webservices.v4.stubs.replication.FaultReason;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.Status;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.cayenne.ObjectContext;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PaymentServiceImpl implements PaymentPortType {

	private static final Logger logger = Logger.getLogger(PaymentServiceImpl.class);

	private final ITransactionGroupProcessor groupProcessor;

	private final IPaymentGatewayService paymentGatewayService;

	private final ICayenneService cayenneService;

	private final IPaymentService paymentInService;

	private final IEnrolmentService enrolService;

	private final ITransactionStubBuilder transactionBuilder;

	private final SessionIdGenerator idGenerator;
	
	private final IWillowStubBuilder stubBuilder;
	
	private final PreferenceControllerFactory prefsFactory;

	@Inject
	public PaymentServiceImpl(ITransactionGroupProcessor groupProcessor, IPaymentGatewayService paymentGatewayService,
			ICayenneService cayenneService, IPaymentService paymentInService, IEnrolmentService enrolService,
			ITransactionStubBuilder transactionBuilder, IWillowStubBuilder stubBuilder, PreferenceControllerFactory prefsFactory) {
		super();
		this.groupProcessor = groupProcessor;
		this.paymentGatewayService = paymentGatewayService;
		this.cayenneService = cayenneService;
		this.paymentInService = paymentInService;
		this.enrolService = enrolService;
		this.transactionBuilder = transactionBuilder;
		this.stubBuilder = stubBuilder;
		this.prefsFactory = prefsFactory;
		this.idGenerator = new SessionIdGenerator();
	}

	/**
	 * Process paymentIn and enrolments. Firstly, saves paymentIn and related
	 * objects, then check available enrolment places. If no places available
	 * abandons payment, generates session id for credit card payments.
	 */
	@Override
	public TransactionGroup processPayment(TransactionGroup transaction) throws ReplicationFault {

		List<ReplicatedRecord> replicatedRecords = Collections.emptyList();
		List<Enrolment> enrolments = new ArrayList<Enrolment>();
		PaymentIn paymentIn = null;
		boolean isCreditCardPayment = false;

		ObjectContext newContext = cayenneService.newContext();

		try {
			// check if group from angel is empty
			if (transaction.getAttendanceOrBinaryDataOrBinaryInfo() == null
					|| transaction.getAttendanceOrBinaryDataOrBinaryInfo().isEmpty()) {
				throw new Exception("Got an empty paymentIn transaction group from angel.");
			}

			isCreditCardPayment = ReplicationUtils.isCreditCardPayment(transaction);
			replicatedRecords = groupProcessor.processGroup(transaction);

			// check if records were saved successfully
			if (replicatedRecords.get(0).getStatus() != Status.SUCCESS) {
				// records wasn't saved, immediately return to angel.
				throw new Exception("Willow was unable to save paymentIn transaction group.");
			}

			for (ReplicatedRecord r : replicatedRecords) {

				if (ReplicationUtils.getEntityName(Enrolment.class).equalsIgnoreCase(r.getStub().getEntityIdentifier())) {

					Enrolment enrolment = (Enrolment) newContext.localObject(
							enrolService.loadById(r.getStub().getWillowId()).getObjectId(), null);

					enrolments.add(enrolment);

				} else if (ReplicationUtils.getEntityName(PaymentIn.class).equalsIgnoreCase(r.getStub().getEntityIdentifier())) {

					PaymentIn p = paymentInService.paymentInByWillowId(r.getStub().getWillowId());

					if (p == null) {
						throw new Exception(String.format(
								"The paymentIn record with angelId:%s wasn't saved during the payment group processing.", r.getStub()
										.getAngelId()));
					}

					paymentIn = (PaymentIn) newContext.localObject(p.getObjectId(), null);
				}
			}

		} catch (Exception e) {
			logger.error("Unable to process payment in.", e);
			FaultReason faultReason = new FaultReason();
			faultReason.setFaultCode(FaultCode.GENERIC_EXCEPTION);
			faultReason.setDetailMessage(String.format("Unable to process payment in. Willow exception: %s", e.getMessage()));
			throw new ReplicationFault("Unable to process payment in.", faultReason);
		}

		// payment is saved at this point, that means that we should return
		// response with status of original payment not matter what happens.

		try {
			TransactionGroup response = new TransactionGroup();
			List<PaymentIn> updatedPayments = new LinkedList<PaymentIn>();
			updatedPayments.add(paymentIn);
			
			// check places
			boolean isPlacesAvailable = true;

			for (Enrolment enrolment : enrolments) {
				CourseClass clazz = enrolment.getCourseClass();
				int availPlaces = clazz.getMaximumPlaces() - clazz.getValidEnrolments().size();
				if (availPlaces < 0) {
					logger.info(String.format("No places available for courseClass:%s.", clazz.getId()));
					isPlacesAvailable = false;
					break;
				}
			}
			
			PreferenceController prefsController = prefsFactory.getPreferenceController(paymentIn.getCollege());
			
			if (isCreditCardPayment && !prefsController.getLicenseCCProcessing()) {
				updatedPayments.add(paymentIn.abandonPayment());
			} else if (!isPlacesAvailable) {
				paymentIn.setStatus(PaymentStatus.FAILED_NO_PLACES);
				updatedPayments.add(paymentIn.abandonPayment());
			} else {
				// if credit card and not-zero payment, generate sessionId.
				if (isCreditCardPayment && paymentIn.getAmount().compareTo(BigDecimal.ZERO) != 0) {
					paymentIn.setSessionId(idGenerator.generateSessionId());
				} else {
					paymentIn.succeed();
				}
			}

			newContext.commitChanges();
			
			Set<ReplicationStub> updatedStubs = transactionBuilder.createPaymentInTransaction(updatedPayments);
			response.getAttendanceOrBinaryDataOrBinaryInfo().addAll(updatedStubs);
			return response;
			
		} catch (Exception e) {
			logger.error(String.format("Exception happened after paymentIn:%s was saved. ", paymentIn.getId()), e);
			return plainPaymentEnrolmentResponse(paymentIn, enrolments);
		}
	}

	/**
	 * Processes refunds.
	 */
	@Override
	public TransactionGroup processRefund(TransactionGroup refundGroup) throws ReplicationFault {
		try {
			// check if paymentOut group is empty
			if (refundGroup.getAttendanceOrBinaryDataOrBinaryInfo() == null
					|| refundGroup.getAttendanceOrBinaryDataOrBinaryInfo().isEmpty()) {
				throw new Exception("Got an empty paymentOut transaction group from angel.");
			}

			// save payment out to database
			List<ReplicatedRecord> replicatedRecords = groupProcessor.processGroup(refundGroup);

			if (replicatedRecords.get(0).getStatus() != Status.SUCCESS) {
				throw new Exception("Willow was unable to save paymentOut transaction group.");
			}

			ReplicatedRecord paymentOutRecord = ReplicationUtils.replicatedPaymentOutRecord(replicatedRecords);
			PaymentOut paymentOut = paymentInService.paymentOutByAngelId(paymentOutRecord.getStub().getAngelId());

			if (paymentOut == null) {
				throw new Exception("The paymentOut record with angelId \"" + paymentOutRecord.getStub().getAngelId()
						+ "\" wasn't saved during the refund group processing.");
			}

			paymentGatewayService.performGatewayOperation(paymentOut);

			TransactionGroup group = new TransactionGroup();
			group.getAttendanceOrBinaryDataOrBinaryInfo().addAll(transactionBuilder.createRefundTransaction(paymentOut));

			return group;
		} catch (Exception e) {
			logger.error("Unable to process refund.", e);
			FaultReason faultReason = new FaultReason();
			faultReason.setFaultCode(FaultCode.GENERIC_EXCEPTION);
			faultReason.setDetailMessage(String.format("Unable to process refund. Willow exception: %s", e.getMessage()));
			throw new ReplicationFault("Unable to process refund.", faultReason);
		}
	}

	/**
	 * Looks for paymentIn by sessionId, if payment is completed success/failed,
	 * then returns soap stubs for paymentIn and related objects. Returns an
	 * empty result otherwise.
	 */
	@Override
	public TransactionGroup getPaymentStatus(String sessionId) throws ReplicationFault {
		try {
			TransactionGroup tGroup = new TransactionGroup();

			List<PaymentIn> pList = paymentInService.getPaymentsBySessionId(sessionId);

			boolean shouldWait = false;

			for (PaymentIn p : pList) {
				if (!p.isAsyncReplicationAllowed()) {
					shouldWait = true;
					break;
				}
			}

			if (!shouldWait) {
				tGroup.getAttendanceOrBinaryDataOrBinaryInfo().addAll(transactionBuilder.createPaymentInTransaction(pList));
			}

			return tGroup;
		} catch (Exception e) {
			logger.error("Unable to get payment status.", e);
			FaultReason faultReason = new FaultReason();
			faultReason.setFaultCode(FaultCode.GENERIC_EXCEPTION);
			faultReason.setDetailMessage(String.format("Unable to process refund. Willow exception: %s", e.getMessage()));
			throw new ReplicationFault("Unable to process refund.", faultReason);
		}
	}
	
	/**
	 * Transaction group which contains paymentIn and related enrolments only.
	 * @param paymentIn
	 * @param enrolments
	 * @return
	 */
	private TransactionGroup plainPaymentEnrolmentResponse(PaymentIn paymentIn, List<Enrolment> enrolments) {
		TransactionGroup response = new TransactionGroup();
		response.getAttendanceOrBinaryDataOrBinaryInfo().add(stubBuilder.convert(paymentIn));
		for (Enrolment enrl : enrolments) {
			response.getAttendanceOrBinaryDataOrBinaryInfo().add(stubBuilder.convert(enrl));
		}
		return response;
	}
}
