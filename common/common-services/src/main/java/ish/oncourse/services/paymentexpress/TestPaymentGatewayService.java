package ish.oncourse.services.paymentexpress;

import ish.common.types.CreditCardType;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.PaymentTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * Test payment gateway processing.
 *
 * @author ksenia
 */
public class TestPaymentGatewayService implements IPaymentGatewayService {


    public static final String NOT_SUPPORTED = "This credit card type is not supported.";
    public static final String CVV_ERROR = "CVV number error.";
    public static final String NAME_ERROR = "Credit Card name error.";
    public static final String EXPIPE_ERROR = "Credit Card expire error.";
    public static final String PAYMETN_APPROVE = "Payment approved.";
    public static final String DATA_NOT_CORRECT = "Input data is not correct.";
    public static final String NUMBER_ERROR = "Invalid credit card number.";

    private static final Logger LOG = Logger.getLogger(TestPaymentGatewayService.class);
    private ICayenneService cayenneService;



    /**
     * Valid credit cards for payment.
     */
    public static final CreditCart MASTERCARD = new CreditCart(CreditCardType.MASTERCARD, "5105105105105100", "JOHN MASTER", "321", "11/2027");
    public static final CreditCart VISA = new CreditCart(CreditCardType.VISA, "4012888888881881", "JOHN VISA", "123", "12/2027");
    public static final CreditCart AMEX = new CreditCart(CreditCardType.AMEX, "378282246310005", "JOHN AMEX", "0965", "07/2027");

    private VerifyResult result;

    public VerifyResult getResult() {
        return result;
    }

    public TestPaymentGatewayService(ICayenneService cayenneService) {
        this.cayenneService = cayenneService;
    }


    private VerifyResult verifyPayment(PaymentIn payment) {

        VerifyResult result = new VerifyResult();

        CreditCart creditCart;

        switch (payment.getCreditCardType()) {

            case VISA:
                creditCart = VISA;
                break;
            case MASTERCARD:
                creditCart = MASTERCARD;
                break;
            case AMEX:
                creditCart = AMEX;
                break;
            default:
                result.addStatusNote(NOT_SUPPORTED);
                result.setSuccess(false);
                return result;
        }

        if (payment.validateCCNumber() != null) {
            result.addStatusNote(NUMBER_ERROR);
        }
        if (!payment.validateCVV()) {
            result.addStatusNote(CVV_ERROR);
        }
        if (!payment.validateCCName()) {
            result.addStatusNote(NAME_ERROR);
        }
        if (!payment.validateCCExpiry()) {
            result.addStatusNote(EXPIPE_ERROR);
        }
        if (!result.getStatusNotes().isEmpty()) {
            result.setSuccess(false);
            LOG.warn(result.getStatusNotes());
            return result;
        }

        if (payment.getCreditCardNumber().equals(creditCart.getNumber()) &&
                payment.getCreditCardCVV().equals(creditCart.getCvv()) &&
                payment.getCreditCardName().equals(creditCart.getName()) &&
                payment.getCreditCardExpiry().equals(creditCart.getExpiry())) {

            result.addStatusNote(PAYMETN_APPROVE);
            result.setSuccess(true);
        } else {
            result.addStatusNote(DATA_NOT_CORRECT);
            result.setSuccess(false);
            LOG.warn(String.format("Input data: CC Number:%s or CVV number: %s or CC name:%s or CC expire: %s is not correct.",
                    payment.getCreditCardNumber(),
                    payment.getCreditCardCVV(),
                    payment.getCreditCardName(),
                    payment.getCreditCardExpiry()));
        }
        return result;
    }


    /**
     * {@inheritDoc} <br/>
     * Success if payment information matches with testing credit cards,
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
            paymentTransaction.setTxnReference(payment.getClientReference());
            context.commitChanges();

            PaymentIn local = context.localObject(payment);
            paymentTransaction.setPayment(local);

            /**
             * Emulate of the rail payment behavior
             */
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
            }

            result = verifyPayment(payment);

            if (result.isSuccess()) {
                paymentTransaction.setResponse(result.getStatusNotes());

                payment.setGatewayResponse(result.getStatusNotes());
                payment.setStatusNotes(result.getStatusNotes());
                payment.setGatewayReference(paymentTransaction.getTxnReference());

                payment.succeed();

            } else {
                paymentTransaction.setResponse(result.statusNotes);

                payment.setStatusNotes(result.statusNotes);
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


    /**
     * Inner class for verification payment data.
     */
    public static class VerifyResult {

        private String statusNotes = StringUtils.EMPTY;
        private boolean success = true;

        public String getStatusNotes() {
            return statusNotes;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public void addStatusNote(String note) {
            statusNotes = new StringBuffer(statusNotes).append(note).append("\n").toString();
        }
    }

    public static class CreditCart {

        private CreditCardType type;
        private String number;
        private String name;
        private String cvv;
        private String expiry;

        public CreditCart(CreditCart creditCart){
            this.type = creditCart.getType();
            this.number = creditCart.getNumber();
            this.name = creditCart.getName();
            this.cvv = creditCart.getCvv();
            this.expiry = creditCart.getExpiry();
        }

        public CreditCart(CreditCardType type,
                          String number,
                          String name,
                          String cvv,
                          String expiry) {
            this.type = type;
            this.number = number;
            this.name = name;
            this.cvv = cvv;
            this.expiry = expiry;
        }

        public CreditCardType getType() {
            return type;
        }

        public void setType(CreditCardType type) {
            this.type = type;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCvv() {
            return cvv;
        }

        public void setCvv(String cvv) {
            this.cvv = cvv;
        }

        public String getExpiry() {
            return expiry;
        }

        public void setExpiry(String expiry) {
            this.expiry = expiry;
        }
    }
}