package ish.oncourse.webservices.utils;

import ish.common.types.PaymentStatus;
import ish.oncourse.model.*;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.services.ParallelExecutor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static ish.oncourse.webservices.utils.PaymentProcessController.PaymentProcessState.*;

public class PaymentProcessController {

    private static final Logger LOGGER = Logger.getLogger(PaymentProcessController.class);

    /**
     * Can be null. in this way payment gateway processing will be run form the same thread.
     */
    private ParallelExecutor parallelExecutor;
    private IPaymentGatewayService paymentGatewayService;
    private PaymentIn paymentIn;
    private ObjectContext objectContext;

    private Throwable throwable;

    private boolean illegalState = false;

    private PaymentProcessState currentState = null;

    private Future<PaymentIn> paymentProcessFuture;

    private Future<Boolean> stackedPaymentMonitorFuture;

    private List<Invoice> invoices;


    public void setParallelExecutor(ParallelExecutor parallelExecutor) {
        this.parallelExecutor = parallelExecutor;
    }

    public void setPaymentGatewayService(IPaymentGatewayService paymentGatewayService) {
        this.paymentGatewayService = paymentGatewayService;
    }

    public PaymentIn getPaymentIn() {
        return paymentIn;
    }

    public void setPaymentIn(PaymentIn paymentIn) {
        this.paymentIn = (PaymentIn) objectContext.localObject(paymentIn.getObjectId(), null);
        processAction(PaymentAction.INIT_PAYMENT);
    }

    public void setObjectContext(ObjectContext objectContext) {
        this.objectContext = objectContext;
    }


    public synchronized void processAction(PaymentAction action) {
        illegalState = !validateAction(action);
        if (illegalState)
            return;
        if (currentState == ERROR)
            return;
        switch (action) {
        	case INIT_PAYMENT:
        		changeProcessState(NOT_PROCESSED);
        		break;
            case MAKE_PAYMENT:
                processPayment();
                break;
            case TRY_ANOTHER_CARD:
                tryOtherCard();
                break;
            case ABANDON_PAYMENT:
            case CANCEL_PAYMENT:
                abandonPaymentReverseInvoice();
                break;
            case ABANDON_PAYMENT_KEEP_INVOICE:
                abandonPaymentKeepInvoice();
                break;
            case UPDATE_PAYMENT_GATEWAY_STATUS:
                updatePaymentGatewayStatus();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private void changeProcessState(PaymentProcessState state) {
        //reset illegalState for every correct state changing
        illegalState = false;
        currentState = state;
        if (stackedPaymentMonitorFuture != null) {
            stackedPaymentMonitorFuture.cancel(true);
            stackedPaymentMonitorFuture = null;
        }
        if (!SUCCESS.equals(currentState) && !CANCEL.equals(currentState)) {
        	//we should not fire watchdog in case if payment already success or canceled for any reasons.
        	stackedPaymentMonitorFuture = parallelExecutor.invoke(new StackedPaymentMonitor(this));
        }
    }

    private boolean validateAction(PaymentAction action) {
        if (currentState == ERROR)
            return true;
        switch (action) {
        	case INIT_PAYMENT:
        		return currentState == null;
            case MAKE_PAYMENT:
            case CANCEL_PAYMENT:
                if (currentState != NOT_PROCESSED)
                    return false;
                break;
            case ABANDON_PAYMENT:
            	if (NOT_PROCESSED.equals(currentState)) {
            		//this may mean only the one case (payment expired without user activity with StackedPaymentMonitor)
            		return true;
            	}   
            case TRY_ANOTHER_CARD:
            case ABANDON_PAYMENT_KEEP_INVOICE:
                if (currentState != FAILED)
                    return false;
                break;
            case UPDATE_PAYMENT_GATEWAY_STATUS:
                if (currentState != PROCESSING_PAYMENT)
                    return false;
                break;
            default:
                throw new IllegalArgumentException();
        }
        return true;
    }

    private void processPayment() {
        changeProcessState(PROCESSING_PAYMENT);
        paymentProcessFuture = parallelExecutor.invoke(new ProcessPaymentInvokable(this));
    }

    private void updatePaymentGatewayStatus() {
        try {
        	if(paymentProcessFuture != null)
        		paymentProcessFuture.get(100, TimeUnit.MILLISECONDS);
            if (paymentIn.getStatus().equals(PaymentStatus.SUCCESS)) {
                changeProcessState(SUCCESS);
            } else {
                changeProcessState(FAILED);
            }
            LOGGER.info(String.format("Payment gateway processing has been finished with status %s", paymentIn.getStatus()));
        } catch (InterruptedException e) {
            setThrowable(e);
        } catch (ExecutionException e) {
            setThrowable(e);
        } catch (TimeoutException e) {
            LOGGER.info("Payment is processed yet");
        }

    }


    /**
     * Abandon payment, reverses invoices
     *
     * @return abandon payment message block
     * @throws java.net.MalformedURLException
     */
    private void abandonPaymentReverseInvoice() {
        changeProcessState(PROCESSING_ABANDON);
        PaymentInAbandonUtil.abandonPaymentReverseInvoice(paymentIn);
        changeProcessState(CANCEL);
    }

    /**
     * Abandon payment, but keeps invoices for later processing.
     *
     * @return abandon payment message block
     */
    private void abandonPaymentKeepInvoice() {
        changeProcessState(PROCESSING_ABANDON);
        paymentIn.abandonPaymentKeepInvoice();
        objectContext.commitChanges();
        changeProcessState(CANCEL);
    }

    /**
     * Marks current trasaction as finalized, creates a new one for other credit
     * card details.
     *
     * @return payment form component
     */
    private void tryOtherCard() {
        changeProcessState(PROCESSING_TRY_OTHER_CARD);
        this.paymentIn = paymentIn.makeCopy();
        this.paymentIn.setStatus(PaymentStatus.IN_TRANSACTION);
        objectContext.commitChanges();
        changeProcessState(NOT_PROCESSED);
    }

    public synchronized PaymentProcessState getCurrentState() {
        return currentState;
    }

    public synchronized List<Invoice> getInvoices() {
        if (invoices == null) {
            invoices = new ArrayList<Invoice>();
            for (PaymentInLine paymentLine : paymentIn.getPaymentInLines()) {
                this.invoices.add(paymentLine.getInvoice());
            }
            Ordering ordering = new Ordering(Invoice.INVOICE_NUMBER_PROPERTY, SortOrder.ASCENDING);
            ordering.orderList(this.invoices);
        }
        return invoices;
    }

    public synchronized College getCollege() {
        return paymentIn.getCollege();
    }


    public synchronized BigDecimal getAmount() {
        return paymentIn.getAmount();
    }

    public Contact getContact() {
        return paymentIn.getContact();
    }

    public PaymentIn performGatewayOperation() {
        paymentGatewayService.performGatewayOperation(paymentIn);
        if (paymentIn.getStatus().equals(PaymentStatus.SUCCESS))
            objectContext.commitChanges();
        return paymentIn;
    }


    public synchronized boolean isProcessFinished() {
        return currentState.equals(CANCEL) ||
                currentState.equals(SUCCESS) ||
                currentState.equals(ERROR);
    }

    public synchronized Throwable geThrowable() {
        return throwable;
    }

    public synchronized void setThrowable(Throwable throwable) {
        this.currentState = ERROR;
        this.throwable = throwable;
        LOGGER.error("Unexpected error", throwable);
    }

    public synchronized boolean isIllegalState() {
        return illegalState;
    }

    public static enum PaymentProcessState {
        NOT_PROCESSED,   //initial state of the controller
        PROCESSING_PAYMENT, //payment gateway is processing the payment
        PROCESSING_ABANDON, //payment abandon processing
        PROCESSING_TRY_OTHER_CARD, //try other card processing
        SUCCESS, //finished status when payment was processed successfully
        FAILED, //finished status when payment was failed.
        CANCEL, //finished status when user canceled processing
        ERROR;  //finished status when unexpected error

        public static boolean isProcessingState(PaymentProcessState state) {
            switch (state) {

                case NOT_PROCESSED:
                case SUCCESS:
                case FAILED:
                case CANCEL:
                case ERROR:
                    return false;
                case PROCESSING_PAYMENT:
                case PROCESSING_ABANDON:
                case PROCESSING_TRY_OTHER_CARD:
                    return true;
                default:
                    throw new IllegalArgumentException(String.format("State %s is not supported", state));
            }
        }
    }

    public static enum PaymentAction {
    	INIT_PAYMENT,//initial action, should be called only once when paymentIn  setted to controller
        MAKE_PAYMENT,
        CANCEL_PAYMENT,
        TRY_ANOTHER_CARD,
        ABANDON_PAYMENT,
        ABANDON_PAYMENT_KEEP_INVOICE,
        UPDATE_PAYMENT_GATEWAY_STATUS
    }

}
