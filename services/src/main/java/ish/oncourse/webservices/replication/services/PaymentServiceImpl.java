package ish.oncourse.webservices.replication.services;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.services.enrol.IEnrolmentService;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.preference.PreferenceControllerFactory;
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
import org.apache.cayenne.ObjectId;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PaymentServiceImpl implements InternalPaymentService {

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
	public GenericTransactionGroup processPayment(GenericTransactionGroup transaction) throws InternalReplicationFault {

		List<GenericReplicatedRecord> replicatedRecords = new ArrayList<>();
		List<Enrolment> enrolments = new ArrayList<>();
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

			GenericReplicatedRecord record = replicatedRecords.get(0);
			// check if records were saved successfully
           if (!replicatedRecords.get(0).isSuccessStatus()) {
				// records wasn't saved, immediately return to angel.
				throw new Exception(String.format("Willow was unable to save paymentIn transaction group. ReplicationRecord error: %s",record.getMessage()));
			}

			for (GenericReplicatedRecord r : replicatedRecords) {

				if (ReplicationUtils.getEntityName(Enrolment.class).equalsIgnoreCase(r.getStub().getEntityIdentifier())) {

					Enrolment enrolment = (Enrolment) newContext.localObject(enrolService.loadById(r.getStub().getWillowId()));
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

					paymentIn = (PaymentIn) newContext.localObject(p);
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
			
			//we also should check that this payment not linked with any invoices which also linked with another in transaction payments.
			boolean isConflictPayment = isHaveConflictedInInvoices(paymentIn, updatedPayments);
			if (!isConflictPayment) {
				//check that payment not contain incorrect enrollments
				if (isEnrolmentsCorrect(enrolments)) {
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
						if (isCreditCardPayment && paymentIn.getAmount().compareTo(Money.ZERO) != 0) {
							paymentIn.setSessionId(idGenerator.generateSessionId());
						} else {
							paymentIn.succeed();
						}
					}
				} else {
					//we should fail the payments if they are for incorrect invoices
					logger.error(String.format("Payment with id = %s will be abandoned to prevent illegal enrolment processing.", paymentIn.getId()));
					updatedPayments.add(paymentIn.abandonPayment());
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
	 * This workaround for #16100 to avoid payments for failed enrollments processing should be removed because original issue fixed.
	 * May be safety removed after all the colleges migrate to angel 4.0 
	 */
	@Deprecated
	public boolean isEnrolmentsCorrect(List<Enrolment> enrolments) {
		for (Enrolment enrolment : enrolments) {
			boolean isZeroOwing = false;
			if (enrolment.getOriginalInvoiceLine() != null && enrolment.getOriginalInvoiceLine().getInvoice() != null) {
				Invoice invoice = enrolment.getOriginalInvoiceLine().getInvoice();
				invoice.updateAmountOwing();//TODO: check can we remove owing
				isZeroOwing = Money.isZeroOrEmpty(invoice.getAmountOwing());
			}
			if (isZeroOwing && EnrolmentStatus.FAILED.equals(enrolment.getStatus())) {
				logger.error(String.format("Failed enrolment with id =  %s linked with 0-owing invoice try to process. Payment will be abandoned.", enrolment.getId()));
				return false;
			}
		}
		return true;
	}
	
	/**
	 * This method check is payment have invoices linked with other payments with in transaction status.
	 * This check required to avoid over payment for the same invoices.
	 * Reverse invoice will be created for conflicted "enrollment" invoices with in transaction enrollment statuses.
	 * May be safety removed after all the colleges migrate to angel 4.0.
	 */
	@Deprecated
	public boolean isHaveConflictedInInvoices(PaymentIn paymentIn, List<PaymentIn> updatedPayments) {
		//we also should check that this payment not linked with any invoices which also linked with another in transaction payments.
		List<ObjectId> conflictedInvoicesObjectIds = getLinesForConflictedInvoices(paymentIn);
		boolean isConflictPayment = !conflictedInvoicesObjectIds.isEmpty();
		if (isConflictPayment) {
			//we should fail this payment and also create create reverse invoices for "in transaction" enrollment not-conflict invoices
			paymentIn.failPayment();//TODO: possible we just need to call payment set status failed here?
			for (PaymentInLine paymentInLine : paymentIn.getPaymentInLines()) {
				Invoice invoice = paymentInLine.getInvoice();
				boolean isEnrollmentInvoice = false;
				for (InvoiceLine invoiceLine : invoice.getInvoiceLines()) {
					//currently we should not create reverse invoice and payments in case if this isn't a enrollment invoice or this invoice 
					//previously have been processed with keep invoice.
					if (invoiceLine.getEnrolment() != null && EnrolmentStatus.IN_TRANSACTION.equals(invoiceLine.getEnrolment().getStatus())) {
						isEnrollmentInvoice = true;
						break;
					}
				}
				//TODO the code should be re-factored because the method only checks there are other payments in transaction or no.
				if (!conflictedInvoicesObjectIds.contains(invoice.getObjectId()) && isEnrollmentInvoice) {
					//we should create reverse payments and invoices
					updatedPayments.add(PaymentIn.createRefundInvoice(paymentInLine, paymentIn.getModified()));
				}
			}
			updatedPayments.add(paymentIn);
		}
		return isConflictPayment;
	}
	
	/**
	 * This method check is payment have invoices linked with other payments with in transaction status.
	 * List of conflicted invoices ObjectIds returned.
	 * May be safety removed after all the colleges migrate to angel 4.0.
	 */
	@Deprecated
	public List<ObjectId> getLinesForConflictedInvoices(PaymentIn paymentForCheck) {
		List<ObjectId> result = new ArrayList<>();
		for (PaymentInLine paymentInLine : paymentForCheck.getPaymentInLines()) {
			Invoice invoice = paymentInLine.getInvoice();
			for (PaymentInLine paymentInLineForCheck : invoice.getPaymentInLines()) {
				PaymentIn paymentIn = paymentInLineForCheck.getPaymentIn();
				if (!paymentIn.getObjectId().equals(paymentForCheck.getObjectId()) && PaymentStatus.IN_TRANSACTION.equals(paymentIn.getStatus())) {
					result.add(paymentInLineForCheck.getInvoice().getObjectId());
					logger.error(String.format("Conflicted invoice with id=%s and amount owing=%s found linked with new payment with id=%s and old " +
						"in transaction payment with id=%s. New payment will be failed to avoid overpayment.", 
						invoice.getId(), invoice.getAmountOwing(), paymentForCheck.getId(), paymentIn.getId()));
				}
			}
		}
		return result;
	}

	/**
	 * Processes refunds.
	 */
	@Override
	public GenericTransactionGroup processRefund(GenericTransactionGroup refundGroup) throws InternalReplicationFault {
		try {
			// check if paymentOut group is empty
			if (refundGroup.getAttendanceOrBinaryDataOrBinaryInfo() == null
					|| refundGroup.getAttendanceOrBinaryDataOrBinaryInfo().isEmpty()) {
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

			boolean shouldWait = false;

			for (PaymentIn p : pList) {
				if (!p.isAsyncReplicationAllowed()) {
					shouldWait = true;
					break;
				}
			}

			if (!shouldWait) {
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
