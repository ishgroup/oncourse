package ish.oncourse.webservices.utils;

import ish.common.types.PaymentStatus;
import ish.oncourse.model.*;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.Cayenne;
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
	private ICayenneService cayenneService;
    private PaymentIn paymentIn;
    private ObjectContext objectContext;

    private Throwable throwable;

    private boolean illegalState = false;

    private PaymentProcessState currentState = INIT;

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
    }

    public void setObjectContext(ObjectContext objectContext) {
        this.objectContext = objectContext;
    }


    public synchronized void processAction(PaymentAction action) {
		if (currentState == ERROR)
			return;

		illegalState = !validateState(action);
        if (illegalState)
            return;

		if (!validateDatabaseState(action))
		{
			setThrowable(new IllegalStateException(String.format("paymentIn with sessionId %s has been changed by another process.",
					paymentIn.getSessionId())));
			return;
		}
		switch (action) {
        	case INIT_PAYMENT:
        		changeProcessState(FILL_PAYMENT_DETAILS);
        		break;
            case MAKE_PAYMENT:
                processPayment();
                break;
            case TRY_ANOTHER_CARD:
                tryOtherCard();
                break;
            case ABANDON_PAYMENT:
            case CANCEL_PAYMENT:
		    case EXPIRE_PAYMENT:
                abandonPaymentReverseInvoice(action);
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
        if (!SUCCESS.equals(currentState) && !CANCEL.equals(currentState) ) {
        	//we should not fire watchdog in case if payment already success or canceled for any reasons.
        	stackedPaymentMonitorFuture = parallelExecutor.invoke(new StackedPaymentMonitor(this));
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
		PaymentIn paymentIn = Cayenne.objectForPK(tempContext, PaymentIn.class, this.paymentIn.getId());


		LOGGER.info(String.format("PaymentAction = %s, PaymentProcessController.state = %s; PaymentIn.status = %s; DB.PaymentIn.status = %s",action, currentState, this.paymentIn.getStatus(), paymentIn.getStatus()));

		switch (action) {
			case INIT_PAYMENT:
			case MAKE_PAYMENT:
			case CANCEL_PAYMENT:
			case TRY_ANOTHER_CARD:
			case ABANDON_PAYMENT:
			case ABANDON_PAYMENT_KEEP_INVOICE:
			case EXPIRE_PAYMENT:
				return (paymentIn.getStatus() == PaymentStatus.IN_TRANSACTION);
			case UPDATE_PAYMENT_GATEWAY_STATUS:
				return (paymentIn.getStatus() == PaymentStatus.IN_TRANSACTION ||
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
    private void abandonPaymentReverseInvoice(PaymentAction action) {
        changeProcessState(PROCESSING_ABANDON);
        PaymentInAbandonUtil.abandonPaymentReverseInvoice(paymentIn);
		switch (action)
		{
			case ABANDON_PAYMENT:
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
        changeProcessState(FILL_PAYMENT_DETAILS);
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
                currentState.equals(ERROR) ||
				currentState.equals(EXPIRED);
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

	public synchronized boolean isExpired() {
		return currentState == EXPIRED;
	}


	public synchronized boolean isFinalState()
	{
		return PaymentProcessState.isFinalState(currentState);
	}

	public synchronized boolean isProcessingState()
	{
		return PaymentProcessState.isProcessingState(currentState);
	}

	public ICayenneService getCayenneService() {
		return cayenneService;
	}

	public void setCayenneService(ICayenneService cayenneService) {
		this.cayenneService = cayenneService;
	}

	public static enum PaymentProcessState {
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

    public static enum PaymentAction {

		INIT_PAYMENT(INIT), //initial action, should be called only once when paymentIn is set to controller
        MAKE_PAYMENT(FILL_PAYMENT_DETAILS),
        CANCEL_PAYMENT(FILL_PAYMENT_DETAILS),
		TRY_ANOTHER_CARD(FAILED),
        ABANDON_PAYMENT(FAILED),
        ABANDON_PAYMENT_KEEP_INVOICE(FAILED),
        UPDATE_PAYMENT_GATEWAY_STATUS(PROCESSING_PAYMENT),
		EXPIRE_PAYMENT(FILL_PAYMENT_DETAILS, PROCESSING_PAYMENT, PROCESSING_ABANDON, PROCESSING_TRY_OTHER_CARD, FAILED);

		private PaymentProcessState[] allowedPaymentProcessStates;
		private PaymentAction(PaymentProcessState... allowedPaymentProcessStates) {
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
