package ish.oncourse.services.paymentexpress;

import java.util.Date;

import ish.common.types.CreditCardType;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.PaymentTransaction;

/**
 * Test payment gateway processing.
 * 
 * @author ksenia
 * 
 */
public class TestPaymentGatewayService implements IPaymentGatewayService {

	/**
	 * {@inheritDoc} <br/>
	 * Success if the credit card type is {@link CreditCardType#MASTERCARD},
	 * fail otherwise.
	 * 
	 * @see ish.oncourse.services.paymentexpress.IPaymentGatewayService#performGatewayOperation(ish.oncourse.model.PaymentIn)
	 */
	public void performGatewayOperation(PaymentIn payment) {

		PaymentTransaction paymentTransaction = payment.getObjectContext().newObject(PaymentTransaction.class);

		paymentTransaction.setPayment(payment);

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
