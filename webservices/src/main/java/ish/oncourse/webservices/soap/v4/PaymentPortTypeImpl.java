package ish.oncourse.webservices.soap.v4;

import ish.common.types.PaymentStatus;
import ish.common.types.PaymentType;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.PaymentOutTransaction;
import ish.oncourse.model.PaymentTransaction;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.webservices.ITransactionGroupProcessor;
import ish.oncourse.webservices.replication.services.ReplicationUtils;
import ish.oncourse.webservices.v4.stubs.replication.EnrolmentStub;
import ish.oncourse.webservices.v4.stubs.replication.EnrolmentTransactionStub;
import ish.oncourse.webservices.v4.stubs.replication.PaymentInStub;
import ish.oncourse.webservices.v4.stubs.replication.PaymentTransactionStub;
import ish.oncourse.webservices.v4.stubs.replication.RefundResult;
import ish.oncourse.webservices.v4.stubs.replication.RefundTransactionStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;
import ish.oncourse.webservices.v4.stubs.replication.Status;
import ish.oncourse.webservices.v4.stubs.replication.TransactionResult;
import ish.oncourse.webservices.v4.stubs.replication.TransactionStatus;
import ish.oncourse.webservices.v4.stubs.replication.TransactionStub;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.cayenne.ObjectContext;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.beans.factory.annotation.Autowired;

@WebService(endpointInterface = "ish.oncourse.webservices.soap.v4.PaymentPortType", serviceName = "ReplicationService", portName = "PaymentPortType", targetNamespace = "http://repl.v4.soap.webservices.oncourse.ish/")
public class PaymentPortTypeImpl implements PaymentPortType {

	private static final Logger logger = Logger.getLogger(PaymentPortTypeImpl.class);

	@Inject
	@Autowired
	private ICourseClassService courseClassService;

	@Inject
	@Autowired
	private ITransactionGroupProcessor groupProcessor;
	
	@Inject
	@Autowired
	private IPaymentGatewayService paymentGatewayService;

	@Inject
	@Autowired
	private ICayenneService cayenneService;

	@Inject
	@Autowired
	private IPaymentService paymentInService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ish.oncourse.webservices.soap.v4.PaymentPortType#processPayment(ish.oncourse
	 * .webservices.v4.stubs.replication.TransactionStub)
	 */
	@Override
	public TransactionResult processPayment(TransactionStub transaction) {
		
		TransactionResult result = new TransactionResult();
		result.setTransactionStatus(TransactionStatus.SUCCESS);

		// group enrolments by courseClassId
		Map<Long, List<EnrolmentStub>> classEnrolMap = new HashMap<Long, List<EnrolmentStub>>();

		for (EnrolmentTransactionStub et : transaction.getEnrolmentTransactions()) {
			EnrolmentStub enrolStub = ReplicationUtils.getEnrolmentStub(et.getTransactionGroup());
			Long courseClassId = enrolStub.getCourseClassId();
			List<EnrolmentStub> enrolments = classEnrolMap.get(courseClassId);
			if (enrolments == null) {
				enrolments = new ArrayList<EnrolmentStub>(10);
				classEnrolMap.put(courseClassId, enrolments);
			}
			enrolments.add(enrolStub);
		}

		boolean isPlacesAvailable = true;

		for (Map.Entry<Long, List<EnrolmentStub>> entry : classEnrolMap.entrySet()) {
			CourseClass courseClass = courseClassService.loadByAngelId(entry.getKey());
			if (courseClass != null && courseClass.getAvailableEnrolmentPlaces() < entry.getValue().size()) {
				isPlacesAvailable = false;
				break;
			}
		}

		// places available

		if (isPlacesAvailable) {
			// create save enrolments
			try {

				for (EnrolmentTransactionStub et : transaction.getEnrolmentTransactions()) {
					List<ReplicatedRecord> enrolReplicatedRecords = groupProcessor.processGroup(et.getTransactionGroup());
					
					ReplicatedRecord enrolRecord = ReplicationUtils.replicatedEnrolmentRecord(enrolReplicatedRecords);
					
					result.getReplicatedRecord().addAll(enrolReplicatedRecords);
					
					if (enrolRecord.getStatus() != Status.SUCCESS) {
						logger.error("Failed to save enrolments.");
						result.setTransactionStatus(TransactionStatus.FAILED);
					}					
				}

				// process payment
				PaymentTransactionStub pTransact = transaction.getPaymentTransaction();

				if (pTransact != null) {
					PaymentInStub paymentInStub = ReplicationUtils.getPaymentInStub(pTransact.getTransactionGroup());

					if (paymentInStub != null) {
						if (PaymentType.CREDIT_CARD.getDatabaseValue().equals(paymentInStub.getType())) {

							// save paymentIn record
							List<ReplicatedRecord> paymentInReplicatedRecords = groupProcessor
									.processGroup(pTransact.getTransactionGroup());
								
							result.getReplicatedRecord().addAll(paymentInReplicatedRecords);
							
							ReplicatedRecord paymentInRecord = ReplicationUtils.replicatedPaymentInRecord(paymentInReplicatedRecords);
							
							if (paymentInRecord.getStatus() == Status.SUCCESS) {
								ObjectContext objectContext = cayenneService.newNonReplicatingContext();

								PaymentTransaction paymentTransaction = objectContext.newObject(PaymentTransaction.class);

								PaymentIn paymentInObject = paymentInService.paymentInByAngelId(paymentInStub.getAngelId());

								paymentTransaction.setPayment((PaymentIn) objectContext.localObject(paymentInObject.getObjectId(), null));

								objectContext.commitChanges();

								result.setSessionId(paymentTransaction.getSessionId());
							}
							else {
								result.setTransactionStatus(TransactionStatus.FAILED);
							}
						}
					}
				}
			} catch (Exception e) {
				logger.error("Failed to update payment transaction.", e);
				result.setTransactionStatus(TransactionStatus.FAILED);
			}
		} else {
			// no places available
			result.setTransactionStatus(TransactionStatus.FAILED_NO_PLACES);
		}

		return result;
	}

	@Override
	public RefundResult processRefund(RefundTransactionStub refundStub) {
		RefundResult result = new RefundResult();
		
		//save payment out to database
		List<ReplicatedRecord> replicatedRecords = groupProcessor.processGroup(refundStub.getTransactionGroup());
		result.getReplicatedRecord().addAll(replicatedRecords);
		
		ReplicatedRecord paymentOutRecord = ReplicationUtils.replicatedPaymentOutRecord(replicatedRecords);
		
		if (paymentOutRecord.getStatus() == Status.SUCCESS) {
			PaymentOut paymentOutObject = paymentInService.paymentOutByAngelId(paymentOutRecord.getStub().getAngelId());
			
			ObjectContext objectContext = cayenneService.newNonReplicatingContext();
			
			PaymentOutTransaction refundTransaction = objectContext.newObject(PaymentOutTransaction.class);
			refundTransaction.setPaymentOut((PaymentOut) objectContext.localObject(paymentOutObject.getObjectId(), null));
			
			Date today = new Date();
			refundTransaction.setCreated(today);
			refundTransaction.setModified(today);
			
			objectContext.commitChanges();
			
			paymentGatewayService.performGatewayOperation(paymentOutObject);
			
			objectContext.commitChanges();
			
			if (paymentOutObject.getStatus() == PaymentStatus.SUCCESS) {
				result.setTransactionStatus(TransactionStatus.SUCCESS);
			}
			else {
				result.setTransactionStatus(TransactionStatus.FAILED);
			}
		}
		else {
			result.setTransactionStatus(TransactionStatus.FAILED);
		}
		
		return result;
	}
}
