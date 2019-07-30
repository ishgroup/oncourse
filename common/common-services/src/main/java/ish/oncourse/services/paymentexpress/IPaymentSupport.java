package ish.oncourse.services.paymentexpress;

import com.paymentexpress.stubs.TransactionDetails;
import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import org.apache.cayenne.BaseDataObject;

/**
 * The interface is helper for processing payment express gateway operation. It was introduced to exclude code duplication.
 * Two classes PaymentInSupport and PaymentOutSupport are implemented the interface.
 *
 * @param <P> - payment (PaymentIn or PaymentOut)
 * @param <T> - transaction (PaymentTransaction or PaymentOutTransaction)
 */
public interface IPaymentSupport<P extends Queueable, T extends BaseDataObject> {

    College getCollege();

    TransactionDetails getTransactionDetails();

	TransactionDetails getTransactionDetails(String billingId);
	
    T createTransaction();
    void adjustTransaction(TransactionResult result);
    void commitTransaction();



    P getPayment();
    void adjustPayment(TransactionResult result);

}
