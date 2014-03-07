package ish.oncourse.webservices.replication.services;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.services.enrol.IEnrolmentService;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.preference.PreferenceControllerFactory;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.utils.PaymentInUtil;
import ish.oncourse.utils.SessionIdGenerator;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.replication.services.IReplicationService.InternalReplicationFault;
import ish.oncourse.webservices.replication.v4.builders.ITransactionStubBuilder;
import ish.oncourse.webservices.replication.v4.builders.IWillowStubBuilder;
import ish.oncourse.webservices.soap.v4.FaultCode;
import ish.oncourse.webservices.util.GenericReplicatedRecord;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PaymentServiceImpl implements InternalPaymentService {

	public static final String MESSAGE_activeEnrolmentExists = "Student %s already has a enrollment in transaction for class %s and he/she cannot be enrolled twice.";

	private static final Logger logger = Logger.getLogger(PaymentServiceImpl.class);

	private final ITransactionGroupProcessor groupProcessor;

	private final IPaymentGatewayService paymentGatewayService;

	private final ICayenneService cayenneService;

	private final IPaymentService paymentInService;

	private final IVoucherService voucherService;

	private final IEnrolmentService enrolService;

	private final ITransactionStubBuilder transactionBuilder;

	private final SessionIdGenerator idGenerator;
	
	private final IWillowStubBuilder stubBuilder;
	
	private final PreferenceControllerFactory prefsFactory;

	@Inject
	public PaymentServiceImpl(ITransactionGroupProcessor groupProcessor, IPaymentGatewayService paymentGatewayService,
			ICayenneService cayenneService, IPaymentService paymentInService, IEnrolmentService enrolService, IVoucherService voucherService,
			ITransactionStubBuilder transactionBuilder, IWillowStubBuilder stubBuilder, PreferenceControllerFactory prefsFactory) {
		super();
		this.groupProcessor = groupProcessor;
		this.paymentGatewayService = paymentGatewayService;
		this.cayenneService = cayenneService;
		this.paymentInService = paymentInService;
		this.enrolService = enrolService;
		this.voucherService = voucherService;
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
	public GenericTransactionGroup processPayment(GenericTransactionGroup transaction) throws InternalReplicationFault {

		List<GenericReplicatedRecord> replicatedRecords = new ArrayList<>();
		List<Enrolment> enrolments = new ArrayList<>();
		PaymentIn paymentIn = null;
		boolean isCreditCardPayment = false;

		ObjectContext newContext = cayenneService.newContext();

		try {
			// check if group from angel is empty
			if (transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo() == null
					|| transaction.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty()) {
				throw new Exception("Got an empty paymentIn transaction group from angel.");
			}

			isCreditCardPayment = ReplicationUtils.isCreditCardPayment(transaction);
			replicatedRecords = groupProcessor.processGroup(transaction);

			GenericReplicatedRecord record = replicatedRecords.get(0);
			// check if records were saved successfully
			if (!replicatedRecords.get(0).isSuccessStatus()) {
				// records wasn't saved, immediately return to angel.
				throw new Exception(String.format("Willow was unable to save paymentIn transaction group. ReplicationRecord error: %s",record.getMessage()));
			}

			for (GenericReplicatedRecord r : replicatedRecords) {

				if (ReplicationUtils.getEntityName(Enrolment.class).equalsIgnoreCase(r.getStub().getEntityIdentifier())) {

					Enrolment enrolment = newContext.localObject(enrolService.loadById(r.getStub().getWillowId()));
					// this case deny check for enrollments which active via QE with case Abandon payment keep invoice
					if (!EnrolmentStatus.SUCCESS.equals(enrolment.getStatus())) {
						enrolments.add(enrolment);
					}

				} else if (ReplicationUtils.getEntityName(PaymentIn.class).equalsIgnoreCase(r.getStub().getEntityIdentifier())) {

					PaymentIn p = paymentInService.paymentInByWillowId(r.getStub().getWillowId());

					if (p == null) {
						throw new Exception(String.format(
								"The paymentIn record with angelId:%s willowId: %s wasn't saved during the payment group processing.", r.getStub()
								.getAngelId(),r.getStub().getWillowId()));
					}

					// ignore voucher payments here since further processing is based on money payment
					if (!PaymentType.VOUCHER.equals(p.getType())) {
						paymentIn = newContext.localObject(p);
					}
				}
			}

		} catch (Exception e) {
			logger.error("Unable to process payment in.", e);
			throw new InternalReplicationFault("Unable to process payment in.", FaultCode.GENERIC_EXCEPTION, 
				String.format("Unable to process payment in. Willow exception: %s", e.getMessage()));
		}

		// payment is saved at this point, that means that we should return
		// response with status of original payment not matter what happens.

		try {
			GenericTransactionGroup response = PortHelper.createTransactionGroup(transaction);
			List<PaymentIn> updatedPayments = new LinkedList<>();
			updatedPayments.add(paymentIn);
			updatedPayments.addAll(PaymentInUtil.getRelatedVoucherPayments(paymentIn));

			// check places
			boolean isPlacesAvailable = true;

			Enrolment activeEnrolment = null;

			for (Enrolment enrolment : enrolments) {
				CourseClass clazz = enrolment.getCourseClass();
				List<Enrolment> validEnrolments = clazz.getValidEnrolments();
				int availPlaces = clazz.getMaximumPlaces() - validEnrolments.size();
				if (availPlaces < 0) {
					logger.info(String.format("No places available for courseClass:%s.", clazz.getId()));
					isPlacesAvailable = false;
					break;
				}
				//check that no duplicated enrolments exist
				List<Enrolment> studentEnrolments = ExpressionFactory.matchExp(Enrolment.STUDENT_PROPERTY, enrolment.getStudent()).filterObjects(validEnrolments);
				//we use this flag also to say about duplicated student enrolments
				//see 18618
				if (studentEnrolments.size() > 1) {
					activeEnrolment = enrolments.get(0);
					break;
				}
			}

			PreferenceController prefsController = prefsFactory.getPreferenceController(paymentIn.getCollege());

			if (isCreditCardPayment && !prefsController.getLicenseCCProcessing()) {
				updatedPayments.addAll(paymentIn.abandonPayment());
			} else if (!isPlacesAvailable) {
				paymentIn.setStatus(PaymentStatus.FAILED_NO_PLACES);
				updatedPayments.addAll(paymentIn.abandonPayment());
			} else if (activeEnrolment != null) {
				paymentIn.setStatus(PaymentStatus.FAILED);
				updatedPayments.addAll(paymentIn.abandonPayment());
				String message = String.format(MESSAGE_activeEnrolmentExists, activeEnrolment.getStudent().getFullName(), activeEnrolment.getCourseClass().getUniqueIdentifier());
				paymentIn.setStatusNotes(message);
				logger.info(message);
			} else {
				// if credit card and not-zero payment, generate sessionId.
				if (isCreditCardPayment && paymentIn.getAmount().compareTo(Money.ZERO) != 0) {
					paymentIn.setSessionId(idGenerator.generateSessionId());
				} else {
					paymentIn.succeed();
				}
			}
			
			newContext.commitChanges();
			
			Set<GenericReplicationStub> updatedStubs = transactionBuilder.createPaymentInTransaction(updatedPayments, PortHelper.getVersionByTransactionGroup(transaction));
			response.getGenericAttendanceOrBinaryDataOrBinaryInfo().addAll(updatedStubs);
			return response;
			
		} catch (Exception e) {
			logger.error(String.format("Exception happened after paymentIn:%s was saved. ", paymentIn.getId()), e);
			return plainPaymentEnrolmentResponse(paymentIn, enrolments, PortHelper.getVersionByTransactionGroup(transaction));
		}
	}

	/**
	 * Processes refunds.
	 */
	@Override
	public GenericTransactionGroup processRefund(GenericTransactionGroup refundGroup) throws InternalReplicationFault {
		try {
			// check if paymentOut group is empty
			if (refundGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo() == null
					|| refundGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().isEmpty()) {
				throw new Exception("Got an empty paymentOut transaction group from angel.");
			}

			// save payment out to database
			List<GenericReplicatedRecord> replicatedRecords = groupProcessor.processGroup(refundGroup);

			if (!replicatedRecords.get(0).isSuccessStatus()) {
				throw new Exception("Willow was unable to save paymentOut transaction group.");
			}
			
			GenericReplicatedRecord paymentOutRecord = ReplicationUtils.replicatedPaymentOutRecord(replicatedRecords);
			PaymentOut paymentOutToProcess = paymentInService.paymentOutByAngelId(paymentOutRecord.getStub().getAngelId());

			if (paymentOutToProcess == null) {
				throw new Exception("The paymentOut record with angelId \"" + paymentOutRecord.getStub().getAngelId()
						+ "\" wasn't saved during the refund group processing.");
			}

			ObjectContext context = cayenneService.newNonReplicatingContext();
			PaymentOut paymentOut = context.localObject(paymentOutToProcess);

			paymentGatewayService.performGatewayOperation(paymentOut);

			// commit modifications to payment in nonreplicating context and immediatelly send transaction group back to angel

			context.commitChanges();

			GenericTransactionGroup group = PortHelper.createTransactionGroup(refundGroup);
			group.getGenericAttendanceOrBinaryDataOrBinaryInfo().addAll(transactionBuilder.createRefundTransaction(paymentOut, 
				PortHelper.getVersionByTransactionGroup(refundGroup)));

			return group;
		} catch (Exception e) {
			logger.error("Unable to process refund.", e);
			throw new InternalReplicationFault("Unable to process refund.", FaultCode.GENERIC_EXCEPTION, 
				String.format("Unable to process refund. Willow exception: %s", e.getMessage()));
		}
	}

	/**
	 * Looks for paymentIn by sessionId, if payment is completed success/failed,
	 * then returns soap stubs for paymentIn and related objects. Returns an
	 * empty result otherwise.
	 */
	@Override
	public GenericTransactionGroup getPaymentStatus(String sessionId, final SupportedVersions version) throws InternalReplicationFault {
		try {
			GenericTransactionGroup tGroup = PortHelper.createTransactionGroup(version);

			List<PaymentIn> pList = paymentInService.getPaymentsBySessionId(sessionId);
			List<PaymentIn> voucherPaymentsList = new ArrayList<PaymentIn>();

			boolean shouldWait = false;

			for (PaymentIn p : pList) {
				if (!p.isAsyncReplicationAllowed()) {
					shouldWait = true;
					break;
				} else {
					voucherPaymentsList.addAll(PaymentInUtil.getRelatedVoucherPayments(p));
				}
			}

			if (!shouldWait) {
				//we should also add the voucher payments in this list
				pList.addAll(voucherPaymentsList);
				tGroup.getGenericAttendanceOrBinaryDataOrBinaryInfo().addAll(transactionBuilder.createPaymentInTransaction(pList, version));
			}

			return tGroup;
		} catch (Exception e) {
			logger.error("Unable to get payment status.", e);
			throw new InternalReplicationFault("Unable to get payment status.", FaultCode.GENERIC_EXCEPTION, 
				String.format("Unable to process refund. Willow exception: %s", e.getMessage()));
		}
	}

	/**
	 * Retrieves vouchers matching voucher stubs received in request from willow db and returns back transaction group
	 * consisting of voucher stubs generated for these freshly fetched stubs to enable angel to check if vouchers
	 * being redeemed there are in consistent state with willow data. Vouchers which are currently in use by willow
	 * (have linked payments in non final state) are not included into response transaction group.
	 *
	 * @throws InternalReplicationFault
	 */
	@Override
	public GenericTransactionGroup getVouchers(GenericTransactionGroup transactionGroup, SupportedVersions version) throws InternalReplicationFault {
		try {
			List<Long> voucherIds = new ArrayList<>();
			for (GenericReplicationStub stub : transactionGroup.getAttendanceOrBinaryDataOrBinaryInfo()) {
				if (ReplicationUtils.getEntityName(Voucher.class).equalsIgnoreCase(stub.getEntityIdentifier())) {
					voucherIds.add(stub.getWillowId());
				}
			}

			List<ProductItem> vouchers = voucherService.loadProductItemsByIds(voucherIds);

			GenericTransactionGroup response = PortHelper.createTransactionGroup(version);

			for (ProductItem productItem : vouchers) {
				Voucher voucher = (Voucher) productItem;
				if (!voucher.isInUse()) {
					response.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(stubBuilder.convert(voucher, version));
				}
			}

			return response;
		} catch (Exception e) {
			logger.error("Unable to get voucher info.", e);
			throw new InternalReplicationFault("Unable to get voucher info.", FaultCode.GENERIC_EXCEPTION,
					String.format("Unable to get voucher info. Willow exception: %s", e.getMessage()));
		}
	}
	
	/**
	 * Transaction group which contains paymentIn and related enrolments only.
	 * @param paymentIn
	 * @param enrolments
	 * @return
	 */
	private GenericTransactionGroup plainPaymentEnrolmentResponse(PaymentIn paymentIn, List<Enrolment> enrolments, final SupportedVersions version) {
		GenericTransactionGroup response = PortHelper.createTransactionGroup(version);
		response.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(stubBuilder.convert(paymentIn, version));
		for (Enrolment enrl : enrolments) {
			response.getGenericAttendanceOrBinaryDataOrBinaryInfo().add(stubBuilder.convert(enrl, version));
		}
		return response;
	}
}
