package ish.oncourse.services.paymentexpress;

import com.paymentexpress.stubs.TransactionDetails;
import com.paymentexpress.stubs.TransactionResult2;
import ish.oncourse.model.College;
import ish.oncourse.model.Queueable;
import org.apache.cayenne.CayenneDataObject;

/**
 * The interface is helper for processing payment express gateway operation. It was introduced to exclude code duplication.
 * Two classes PaymentInSupport and PaymentOutSupport are implemented the interface.
 *
 * @param <P> - payment (PaymentIn or PaymentOut)
 * @param <T> - transaction (PaymentTransaction or PaymentOutTransaction)
 */
public interface IPaymentSupport<P extends Queueable, T extends CayenneDataObject> {

    College getCollege();

    TransactionDetails getTransactionDetails();

    T createTransaction();
    void adjustTransaction(TransactionResult2 result);
    void commitTransaction();



    P getPayment();
    void adjustPayment(TransactionResult2 result);

}
