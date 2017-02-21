package ish.oncourse.util.payment;

import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.paymentexpress.INewPaymentGatewayService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import ish.oncourse.services.paymentexpress.PaymentExpressGatewayService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.utils.PaymentInUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ioc.services.ParallelExecutor;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static ish.oncourse.util.payment.PaymentProcessController.PaymentProcessState.*;

public class PaymentProcessController {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Can be null. in this way payment gateway processing will be run form the same thread.
     */
    private ParallelExecutor parallelExecutor;
    private INewPaymentGatewayService paymentGatewayService;
	private ICayenneService cayenneService;
	private IPaymentService paymentService;
	
    private PaymentInModel paymentInModel;
    private ObjectContext objectContext;

    private Runnable commitDelegate;

    /**
     * If the property is true StackedPaymentMonitor is being started. If we don't need start the process just set the property false.
     */
    private boolean startWatcher = true;


    private Throwable throwable;

    private boolean illegalState = false;

    private PaymentProcessState currentState = INIT;

    private Future<PaymentIn> paymentProcessFuture;

    private Future<Boolean> stackedPaymentMonitorFuture;

    private List<Invoice> invoices;

    private Lock lock = new ReentrantLock();

    public PaymentIn getPaymentIn() {
        return paymentInModel.getPaymentIn();
    }
    
    private boolean keepInvoice() {
       return PaymentInUtil.hasSuccessEnrolments(paymentInModel.getPaymentIn()) || PaymentInUtil.hasSuccessProductItems(paymentInModel.getPaymentIn());
    }


    public void processAction(PaymentAction action) {
        lock.lock();
        try {
            if (currentState == ERROR)
                return;

            illegalState = !validateState(action);
            if (illegalState)
                return;

            if (!validateDatabaseState(action))
            {
                setThrowable(new IllegalStateException(String.format("paymentIn id: %s, sessionId: %s, status: %s, state: %s  has been changed by another process.",
                        paymentInModel.getPaymentIn().getId(), paymentInModel.getPaymentIn().getSessionId(), paymentInModel.getPaymentIn().getStatus(), currentState)));
                return;
            }
            switch (action) {
                case INIT_PAYMENT:
                    paymentInModel.getPaymentIn().setStatus(PaymentStatus.CARD_DETAILS_REQUIRED);
                    objectContext.commitChanges();
                    changeProcessState(FILL_PAYMENT_DETAILS);
                    break;
                case MAKE_PAYMENT:
                    processPayment();
                    break;
                case TRY_ANOTHER_CARD:
                    tryOtherCard();
                    break;
                case ABANDON_PAYMENT:
                    abandonPayment(action, keepInvoice());
                    break;
                case CANCEL_PAYMENT:
                    abandonPayment(action, true);
                    break;
                case EXPIRE_PAYMENT:
                    if (paymentService.isProcessedByGateway(paymentInModel.getPaymentIn())) {
                        paymentInModel.getPaymentIn().setStatusNotes(PaymentStatus.PAYMENT_EXPIRED_BY_TIMEOUT_MESSAGE);
                        //if the payment expire by timeout we need to keep invoice
                        abandonPayment(action, true);
                    }
                    break;
                case ABANDON_PAYMENT_KEEP_INVOICE:
                    abandonPayment(action, true);
                    break;
                case UPDATE_PAYMENT_GATEWAY_STATUS:
                    updatePaymentGatewayStatus();
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        } finally {
            lock.unlock();
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
        
        if (!isProcessFinished() && startWatcher) {
        	//we should not fire watchdog in case if payment already success or canceled for any reasons.
        	stackedPaymentMonitorFuture = parallelExecutor.invoke(new StuckPaymentMonitor(this));
        }
    }

    private boolean validateState(PaymentAction action) {
        switch (action) {
        	case INIT_PAYMENT:
            case MAKE_PAYMENT:
            case CANCEL_PAYMENT:
            case ABANDON_PAYMENT:
            case TRY_ANOTHER_CARD:
            case ABANDON_PAYMENT_KEEP_INVOICE:
            case UPDATE_PAYMENT_GATEWAY_STATUS:
			case EXPIRE_PAYMENT:
				return action.validateState(currentState);
            default:
                throw new IllegalArgumentException();
        }
    }

	private  boolean validateDatabaseState(PaymentAction action) {

		ObjectContext tempContext = cayenneService.newNonReplicatingContext();
		PaymentIn paymentIn = Cayenne.objectForPK(tempContext, PaymentIn.class, this.paymentInModel.getPaymentIn().getId());


		logger.info("PaymentAction = {}, PaymentProcessController.state = {}; PaymentIn.status = {}; DB.PaymentIn.status = {}", 
				action, currentState, this.paymentInModel.getPaymentIn().getStatus(), this.paymentInModel.getPaymentIn().getStatus());

		switch (action) {
			case INIT_PAYMENT:
                return (paymentIn.getStatus() == PaymentStatus.IN_TRANSACTION);
			case MAKE_PAYMENT:
			case CANCEL_PAYMENT:
			case TRY_ANOTHER_CARD:
			case ABANDON_PAYMENT:
			case ABANDON_PAYMENT_KEEP_INVOICE:
			case EXPIRE_PAYMENT:
				return (paymentIn.getStatus() == PaymentStatus.CARD_DETAILS_REQUIRED);
			case UPDATE_PAYMENT_GATEWAY_STATUS:
				return (paymentIn.getStatus() == PaymentStatus.CARD_DETAILS_REQUIRED ||
						paymentIn.getStatus() == PaymentStatus.SUCCESS);
			default:
				throw new IllegalArgumentException();

		}
	}

	private void processPayment() {
        changeProcessState(PROCESSING_PAYMENT);
        paymentProcessFuture = parallelExecutor.invoke(new ProcessPaymentInvokable(this));
    }

    private void updatePaymentGatewayStatus() {
        try {
        	if(paymentProcessFuture != null)
        		paymentProcessFuture.get(100, TimeUnit.MILLISECONDS);
            if (paymentInModel.getPaymentIn().getStatus().equals(PaymentStatus.SUCCESS)) {
                changeProcessState(SUCCESS);
            } else if (paymentInModel.getPaymentIn().getStatus().equals(PaymentStatus.IN_TRANSACTION)) {
                changeProcessState(ERROR);
            } else {
                changeProcessState(FAILED);
            }
            logger.info("Payment gateway processing has been finished with status {}", paymentInModel.getPaymentIn().getStatus());
        } catch (InterruptedException | ExecutionException e) {
            setThrowable(e);
        } catch (TimeoutException e) {
            logger.info("Payment is processed yet");
        }

    }

    /**
     * Abandon payment, reverses invoices
     *
     * @return abandon payment message block
     * @throws java.net.MalformedURLException
     */
    private void abandonPayment(PaymentAction action, boolean keepInvoice) {
        changeProcessState(PROCESSING_ABANDON);
        PaymentInAbandon.valueOf(paymentInModel, keepInvoice).perform();
        objectContext.commitChanges();
		switch (action)
		{
			case ABANDON_PAYMENT:
			case ABANDON_PAYMENT_KEEP_INVOICE:
			case CANCEL_PAYMENT:
				changeProcessState(CANCEL);
				break;
			case EXPIRE_PAYMENT:
				changeProcessState(EXPIRED);
				break;
			default:
				throw new IllegalArgumentException();
		}
    }


    /**
     * Marks current trasaction as finalized, creates a new one for other credit
     * card details.
     *
     * @return payment form component
     */
    private void tryOtherCard() {
        changeProcessState(PROCESSING_TRY_OTHER_CARD);
        this.paymentInModel = PaymentInModel.valueOf(this.paymentInModel);
        this.paymentInModel.getPaymentIn().setStatus(PaymentStatus.CARD_DETAILS_REQUIRED);
        objectContext.commitChanges();
        changeProcessState(FILL_PAYMENT_DETAILS);
    }

    public PaymentProcessState getCurrentState() {
        lock.lock();
        try {
            return currentState;
        } finally {
            lock.unlock();
        }
    }

    public List<Invoice> getInvoices() {
        lock.lock();
        try {
            return paymentInModel.getInvoices();
        } finally {
            lock.unlock();
        }
    }

    public College getCollege() {
        lock.lock();
        try {
            return  paymentInModel.getPaymentIn().getCollege();
        } finally {
            lock.unlock();
        }
    }


    public Money getAmount() {
        lock.lock();
        try {
            return paymentInModel.getPaymentIn().getAmount();
        } finally {
            lock.unlock();
        }
    }

    public Contact getContact() {
        lock.lock();
        try {
            return paymentInModel.getPaymentIn().getContact();
        } finally {
            lock.unlock();
        }
    }

    public PaymentIn performGatewayOperation() {
        lock.lock();
        try {
            paymentGatewayService.submit(paymentInModel);
            if (paymentInModel.getPaymentIn().getStatus().equals(PaymentStatus.SUCCESS) ||
                    (paymentInModel.getPaymentIn().getStatus().equals(PaymentStatus.IN_TRANSACTION) && PaymentExpressGatewayService.UNKNOW_RESULT_PAYMENT_IN.equals(paymentInModel.getPaymentIn().getStatusNotes())))
                commitChanges();
            return paymentInModel.getPaymentIn();
        } finally {
            lock.unlock();
        }
    }


    public boolean isProcessFinished() {
        lock.lock();
        try {
            return currentState.equals(CANCEL) ||
                    currentState.equals(SUCCESS) ||
                    currentState.equals(ERROR) ||
                    currentState.equals(EXPIRED);
        } finally {
            lock.unlock();
        }
    }

    public Throwable geThrowable() {
        lock.lock();
        try {
            return throwable;
        } finally {
            lock.unlock();
        }
    }

    public void setThrowable(Throwable throwable) {
        lock.lock();
        try {
            this.currentState = ERROR;
            this.throwable = throwable;
            logger.error("Unexpected error", throwable);
        } finally {
            lock.unlock();
        }
    }

    public boolean isIllegalState() {
        lock.lock();
        try {
            return illegalState;
        } finally {
            lock.unlock();
        }
    }

	public boolean isExpired() {
        lock.lock();
        try {
            return currentState == EXPIRED;
        } finally {
            lock.unlock();
        }
	}
	
	public boolean isFinalState() {
        lock.lock();
        try {
            return PaymentProcessState.isFinalState(currentState);
        } finally {
            lock.unlock();
        }
	}

	public boolean isProcessingState() {
        lock.lock();
        try {
            return PaymentProcessState.isProcessingState(currentState);
        } finally {
            lock.unlock();
        }
	}

    public boolean isWrongPaymentExpressResult() {
        lock.lock();
        try {
            return paymentInModel.getPaymentIn().getStatus().equals(PaymentStatus.IN_TRANSACTION) && currentState == ERROR;
        } finally {
            lock.unlock();
        }
    }

	public ICayenneService getCayenneService() {
		return cayenneService;
	}

    protected void commitChanges() {
        lock.lock();
        try {
            if (commitDelegate != null) {
                commitDelegate.run();
            }
            objectContext.commitChanges();
        } finally {
            lock.unlock();
        }
    }

    public static  PaymentProcessController valueOf(ParallelExecutor parallelExecutor,
                                                    INewPaymentGatewayService paymentGatewayService,
                                                    ICayenneService cayenneService,
                                                    IPaymentService paymentService,
                                                    PaymentInModel paymentInModel) {
        PaymentProcessController result = new PaymentProcessController();
        result.parallelExecutor = parallelExecutor;
        result.paymentGatewayService = paymentGatewayService;
        result.cayenneService = cayenneService;
        result.paymentService = paymentService;

        result.paymentInModel = paymentInModel;
        result.objectContext = paymentInModel.getObjectContext();
        return result;
    }

    public static  PaymentProcessController valueOf(ParallelExecutor parallelExecutor,
                                                    INewPaymentGatewayService paymentGatewayService,
                                                    ICayenneService cayenneService,
                                                    IPaymentService paymentService,
                                                    PaymentInModel paymentInModel, Runnable commitDelegate) {
        PaymentProcessController result = new PaymentProcessController();
        result.parallelExecutor = parallelExecutor;
        result.paymentGatewayService = paymentGatewayService;
        result.cayenneService = cayenneService;
        result.paymentService = paymentService;

        result.paymentInModel = paymentInModel;
        result.objectContext = paymentInModel.getObjectContext();
        result.commitDelegate = commitDelegate;
        result.startWatcher = false;
        return result;
    }


    public enum PaymentProcessState {
		INIT, //initial state of the controller
		FILL_PAYMENT_DETAILS, //payment form is opened
        PROCESSING_PAYMENT, //payment gateway is processing the payment
        PROCESSING_ABANDON, //payment abandon processing
        PROCESSING_TRY_OTHER_CARD, //try other card processing
        SUCCESS, //finished status when payment was processed successfully
        FAILED, //finished status when payment was failed.
        CANCEL, //finished status when user canceled processing
        ERROR, //finished status when unexpected error
		EXPIRED;


		public static boolean isFinalState(PaymentProcessState state) {
			switch (state) {
				case SUCCESS:
				case FAILED:
				case CANCEL:
				case ERROR:
				case EXPIRED:
					return true;
				case PROCESSING_PAYMENT:
				case PROCESSING_ABANDON:
				case PROCESSING_TRY_OTHER_CARD:
				case FILL_PAYMENT_DETAILS:
				case INIT:
					return false;
				default:
					throw new IllegalArgumentException(String.format("State %s is not supported", state));
			}
		}


		public static boolean isProcessingState(PaymentProcessState state) {
            switch (state) {

                case FILL_PAYMENT_DETAILS:
                case SUCCESS:
                case FAILED:
                case CANCEL:
                case ERROR:
				case EXPIRED:
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

    public enum PaymentAction {

		INIT_PAYMENT(INIT), //initial action, should be called only once when paymentIn is set to controller
        MAKE_PAYMENT(FILL_PAYMENT_DETAILS),
        CANCEL_PAYMENT(FILL_PAYMENT_DETAILS),
		TRY_ANOTHER_CARD(FAILED),
        ABANDON_PAYMENT(FAILED),
        ABANDON_PAYMENT_KEEP_INVOICE(FAILED),
        UPDATE_PAYMENT_GATEWAY_STATUS(PROCESSING_PAYMENT),
		EXPIRE_PAYMENT(FILL_PAYMENT_DETAILS, PROCESSING_PAYMENT, PROCESSING_ABANDON, PROCESSING_TRY_OTHER_CARD, FAILED);

		private PaymentProcessState[] allowedPaymentProcessStates;
        PaymentAction(PaymentProcessState... allowedPaymentProcessStates) {
			this.allowedPaymentProcessStates = allowedPaymentProcessStates;
		}

		public boolean validateState(PaymentProcessState currentState)
		{
			for (PaymentProcessState allowedPaymentProcessState : allowedPaymentProcessStates) {
				if (currentState == allowedPaymentProcessState)
					return true;
			}
			return false;
		}
	}
}
