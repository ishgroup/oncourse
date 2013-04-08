package ish.oncourse.services.paymentexpress;

import ish.common.types.CreditCardType;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.PaymentTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;

import java.util.Date;

/**
 * Test payment gateway processing.
 * 
 * @author ksenia
 * 
 */
public class TestPaymentGatewayService implements IPaymentGatewayService {
	
	private ICayenneService cayenneService;
	
	public TestPaymentGatewayService(ICayenneService cayenneService) {
		this.cayenneService = cayenneService;
	}

	/**
	 * {@inheritDoc} <br/>
	 * Success if the credit card type is {@link CreditCardType#MASTERCARD},
	 * fail otherwise.
	 * 
	 * @see ish.oncourse.services.paymentexpress.IPaymentGatewayService#performGatewayOperation(ish.oncourse.model.PaymentIn)
	 */
	public void performGatewayOperation(PaymentIn payment) {
		ObjectContext context = cayenneService.newNonReplicatingContext();

		if (payment.isZeroPayment()) {
			payment.succeed();
		} else {

			PaymentTransaction paymentTransaction = context.newObject(PaymentTransaction.class);
			
			context.commitChanges();

			PaymentIn local = (PaymentIn) context.localObject(payment.getObjectId(), null);
			paymentTransaction.setPayment(local);

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}

			if (payment.getCreditCardType().equals(CreditCardType.MASTERCARD)) {

				payment.succeed();

				paymentTransaction.setResponse("Payment approved.");
				paymentTransaction.setTxnReference("1344X8990");
				payment.setGatewayResponse("Payment approved.");
				payment.setGatewayReference("1344X8990");
				payment.setDateBanked(new Date());

			} else {
				payment.setStatusNotes("Card declined");
				payment.setStatus(PaymentStatus.FAILED_CARD_DECLINED);
				payment.failPayment();
			}
			paymentTransaction.setIsFinalised(true);
			context.commitChanges();
		}
	}

	@Override
	public void performGatewayOperation(PaymentOut paymentOut) {
		if (paymentOut.getTotalAmount().longValue() < 1000) {
			paymentOut.succeed();
			
			Date today = new Date();
			paymentOut.setDateBanked(today);
			paymentOut.setDatePaid(today);
			
		} else {
			paymentOut.failed();
		}
		
	}
}
