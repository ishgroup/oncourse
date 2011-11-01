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
import ish.oncourse.utils.SessionIdGenerator;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.exception.StackTraceUtils;
import ish.oncourse.webservices.replication.builders.ITransactionStubBuilder;
import ish.oncourse.webservices.soap.v4.FaultCode;
import ish.oncourse.webservices.soap.v4.PaymentPortType;
import ish.oncourse.webservices.soap.v4.ReplicationFault;
import ish.oncourse.webservices.v4.stubs.replication.FaultReason;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;

public class PaymentServiceImpl implements PaymentPortType {

	private static final Logger logger = Logger.getLogger(PaymentServiceImpl.class);

	@InjectService("Atomic")
	private ITransactionGroupProcessor groupProcessor;

	@Inject
	private IPaymentGatewayService paymentGatewayService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IPaymentService paymentInService;

	@Inject
	private IEnrolmentService enrolService;

	@Inject
	private ITransactionStubBuilder transactionBuilder;

	private SessionIdGenerator idGenerator;

	public PaymentServiceImpl() {
		this.idGenerator = new SessionIdGenerator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ish.oncourse.webservices.soap.v4.PaymentPortType#processPayment(ish.oncourse
	 * .webservices.v4.stubs.replication.TransactionStub)
	 */
	@Override
	public TransactionGroup processPayment(TransactionGroup transaction) throws ReplicationFault {

		try {
			boolean isCreditCardPayment = ReplicationUtils.isCreditCardPayment(transaction);

			List<ReplicatedRecord> replicatedRecords = groupProcessor.processGroup(transaction);

			ObjectContext newContext = cayenneService.newContext();

			// check places
			List<Enrolment> enrolments = new ArrayList<Enrolment>();
			PaymentIn paymentIn = null;

			for (ReplicatedRecord r : replicatedRecords) {
				
				if (ReplicationUtils.getEntityName(Enrolment.class).equalsIgnoreCase(r.getStub().getEntityIdentifier())) {
					
					Enrolment enrolment = (Enrolment) newContext.localObject(
							enrolService.loadById(r.getStub().getWillowId()).getObjectId(), null);
					
					enrolments.add(enrolment);
					
				} else if (ReplicationUtils.getEntityName(PaymentIn.class).equalsIgnoreCase(r.getStub().getEntityIdentifier())) {
					
					PaymentIn p = paymentInService.paymentInByWillowId(r.getStub().getWillowId());
					
					if (p == null) {
						throw new Exception(String.format("The paymentIn record with angelId:%s wasn't saved during the payment group processing.", r.getStub().getAngelId()));
					}
					
					paymentIn = (PaymentIn) newContext.localObject(p.getObjectId(), null);
				}
				
			}
			
			
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

			List<PaymentIn> updatedPayments = new LinkedList<PaymentIn>();

			if (!isPlacesAvailable) {
				paymentIn.setStatus(PaymentStatus.FAILED_NO_PLACES);
				updatedPayments.add(paymentIn.abandonPayment());
			} else {
				//if credit card and not-zero payment, generate sessionId.
				if (isCreditCardPayment && paymentIn.getAmount().compareTo(BigDecimal.ZERO) != 0) {
					paymentIn.setSessionId(idGenerator.generateSessionId());
				} else {
					paymentIn.succeed();
				}
			}

			updatedPayments.add(paymentIn);

			newContext.commitChanges();

			TransactionGroup group = new TransactionGroup();
			group.getAttendanceOrBinaryDataOrBinaryInfo().addAll(transactionBuilder.createPaymentInTransaction(updatedPayments));

			return group;
		} catch (Exception e) {
			logger.error("Unable to process payment in.", e);
			FaultReason faultReason = new FaultReason();
			faultReason.setFaultCode(FaultCode.GENERIC_EXCEPTION);
			faultReason.setDetailMessage(String.format("Unable to process payment in. Willow exception: %s", StackTraceUtils.stackTraceAsString(e)));
			throw new ReplicationFault("Unable to process payment in.", faultReason);
		}
	}

	@Override
	public TransactionGroup processRefund(TransactionGroup refundGroup) throws ReplicationFault {
		try {
			// save payment out to database
			List<ReplicatedRecord> replicatedRecords = groupProcessor.processGroup(refundGroup);
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
			faultReason.setDetailMessage(String.format("Unable to process refund. Willow exception: %s", StackTraceUtils.stackTraceAsString(e)));
			throw new ReplicationFault("Unable to process refund.", faultReason);
		}
	}

	@Override
	public TransactionGroup getPaymentStatus(String sessionId) throws ReplicationFault {
		try {
			TransactionGroup tGroup = new TransactionGroup();

			List<PaymentIn> pList = paymentInService.getPaymentsBySessionId(sessionId);

			boolean shouldWait = false;

			for (PaymentIn p : pList) {
				if (p.getStatus() == PaymentStatus.IN_TRANSACTION || p.getStatus() == PaymentStatus.CARD_DETAILS_REQUIRED) {
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
			faultReason.setDetailMessage(String.format("Unable to process refund. Willow exception: %s", StackTraceUtils.stackTraceAsString(e)));
			throw new ReplicationFault("Unable to process refund.", faultReason);
		}
	}
}
