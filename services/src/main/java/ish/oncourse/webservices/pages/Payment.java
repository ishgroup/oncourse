package ish.oncourse.webservices.pages;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.webservices.components.PaymentForm;
import ish.oncourse.webservices.components.PaymentResult;
import ish.oncourse.webservices.exception.PaymentNotFoundException;
import ish.oncourse.webservices.utils.PaymentInAbandonUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Invokable;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.ParallelExecutor;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

@Import(stylesheet = "css/screen.css")
public class Payment {
	public static final String PAYMENT_PAGE_NAME = "/Payment/";
	public static final String SESSION_ID_ATTRIBUTE = "sessionId";
	private static final Logger LOGGER = Logger.getLogger(Payment.class);
	private static final String PAYMENT_AMOUNT_FORMAT = "###,##0.00";
	public static final String HTTPS_PROTOCOL = "https://";
	
	@Inject
	private Messages messages;

	@Inject
	private Request request;
	
	@Property
	@Persist
	private PaymentIn payment;
	
	@Property
	private Contact payer;
	
	@Persist
    private ObjectContext context;
	
	@Inject
    private ICayenneService cayenneService;
	
	@Inject
	private IPaymentService paymentService;
	
	@Inject
    private ComponentResources componentResources;
	
	@SuppressWarnings("all")
	@Property
	private Money totalIncGst;
	
	@SuppressWarnings("all")
	@Property
	private Format moneyFormat;
	
	@Property
	private List<Invoice> invoices;

	@SuppressWarnings("all")
	@Property
	private Invoice invoice;
	
	@SuppressWarnings("all")
	@InjectComponent
	private PaymentForm paymentForm;
	
	@Inject
	private IPaymentGatewayService paymentGatewayService;
	
	@Property
	@Persist
	private PaymentStates currentState;
	
	@Inject
	private ParallelExecutor parallelExecutor;
	
	@Property
	@Persist
	private Future<PaymentIn> currentProcessFeature;
	
	@Property
	@Persist
	private AbandonStackedPaymentInvokable actualAbandonStackedPaymentInvokable;
	
	private synchronized void changeCurrentState(final PaymentStates currentState) {
		if (!currentState.equals(this.currentState) || actualAbandonStackedPaymentInvokable == null) {
			//we need to re-invoke the AbandonStackedPaymentInvokable
			if (actualAbandonStackedPaymentInvokable != null) {
				if (!actualAbandonStackedPaymentInvokable.isProcessed()) {
					//we need to cancel current watch dog for re-scheduling
					actualAbandonStackedPaymentInvokable.setCanceled(true);
				}
			}
			// if the payment not null and not in final state we need to invoke new the AbandonStackedPaymentInvokable
			if (payment != null && !isPaymentProcessed() && !isPaymentCanceled(payment)) {
				actualAbandonStackedPaymentInvokable = new AbandonStackedPaymentInvokable(new Date().getTime(), payment);
				parallelExecutor.invoke(actualAbandonStackedPaymentInvokable);
			}
		}
		this.currentState = currentState;
	}

	private void checkContext() {
    	synchronized (this) {
            if (getContext() == null) {
                context = cayenneService.newContext();
            }
        }
    }
	
	/**
     * Clears all the properties with the @Persist annotation.
     */
    public void clearPersistedProperties() {
        componentResources.discardPersistentFieldChanges();
    }
	
	/**
	 * Finds and init payment and payment transaciton by referenceId.
	 * 
	 * @param sessionId
	 */
	void onActivate(String sessionId) {
		checkContext();
		synchronized (context) {
			Session session = request.getSession(true);
			if (sessionId != null && !sessionId.equals(session.getAttribute(SESSION_ID_ATTRIBUTE))) {
				//reset the state for new QE if required
				changeCurrentState(PaymentStates.NOT_PROCESSED);
			}
			if (currentState == null) {
				changeCurrentState(PaymentStates.NOT_PROCESSED);
			}
			if (isShowPaymentForm()) {
				this.payment = paymentService.currentPaymentInBySessionId(sessionId);
				if (payment == null) {
					clearPersistedProperties();
					throw new PaymentNotFoundException(messages.format("payment.not.found", sessionId));
				} else {
					payment = (PaymentIn) getContext().localObject(payment.getObjectId(), null);
					//just for scheduling watch dog.
					changeCurrentState(PaymentStates.NOT_PROCESSED);
				}
			}
			this.moneyFormat = new DecimalFormat(PAYMENT_AMOUNT_FORMAT);
			session.setAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE, payment.getCollege().getId());
			session.setAttribute(SESSION_ID_ATTRIBUTE, sessionId);
			this.totalIncGst = new Money(payment.getAmount());
			this.payer = this.payment.getContact();
			initInvoices();
		}
	}

	private void initInvoices() {
		this.invoices = new ArrayList<Invoice>();		
		for (PaymentInLine paymentLine : this.payment.getPaymentInLines()) {
			this.invoices.add(paymentLine.getInvoice());
		}		
		Ordering ordering = new Ordering(Invoice.INVOICE_NUMBER_PROPERTY, SortOrder.ASCENDING);
		ordering.orderList(this.invoices);
	}
	
	public String getPaymentTitle() {
		return messages.format("payment.title", payer.getGivenName(), payer.getFamilyName());
	}
	
	public String getInvoiceDescription() {
		return messages.format("invoice.desc", invoice.getInvoiceNumber());
	}
	
	/**
     * Method returns true if by some reason persist properties have been cleared.
     * For example: the payment has been processed from other tab of the browser.
     */
    public boolean isPersistCleared() {
        return getContext() == null;
    }
    
    public ObjectContext getContext() {
        return context;
    }
	
	public Object handleUnexpectedException(final Throwable cause) {
    	if (isPersistCleared()) {
			LOGGER.warn("Payment were processed. User used two or more tabs", cause);
			return this;
		} else {
			throw new IllegalArgumentException(cause);
		}
    }
	
	/**
	 * Cancels payment, sends notification, displays cancelled message.
	 * 
	 * @return cancelled message block
	 */
	public Object cancelPayment() {
		abandonPaymentReverseInvoice();
		try {
			return new URL(HTTPS_PROTOCOL  + request.getServerName() + request.getContextPath() + PAYMENT_PAGE_NAME 
				+ request.getSession(false).getAttribute(Payment.SESSION_ID_ATTRIBUTE));
		} catch (MalformedURLException e) {
			return null;
		}
	}
	
	public String getRefreshLink() {
		return HTTPS_PROTOCOL  + request.getServerName() + request.getContextPath() + PAYMENT_PAGE_NAME 
			+ request.getSession(false).getAttribute(Payment.SESSION_ID_ATTRIBUTE);
	}
	
	public void processPayment() {
		synchronized (context) {
			if (PaymentResult.isNewPayment(payment)) {
				// additional check for card details required added to avoid #13172
				changeCurrentState(PaymentStates.IN_PROCESSING);
				currentProcessFeature = parallelExecutor.invoke(new ProcessPaymentInvokable(payment, paymentGatewayService));
			}
		}
	}
	
	/**
	 * Abandon payment, reverses invoices
	 * 
	 * @return abandon payment message block
	 * @throws MalformedURLException 
	 */
	public void abandonPaymentReverseInvoice() {
		if (!isProcessingPayment() && !isPaymentProcessed() && !isPaymentCanceled(payment)) {
			synchronized (context) {
				try {
					changeCurrentState(PaymentStates.IN_PROCESSING);
					PaymentInAbandonUtil.abandonPaymentReverseInvoice(payment);
				} finally {
					changeCurrentState(PaymentStates.PROCESSED);
				}
			}
		} else {
			changeCurrentState(PaymentStates.IN_PROCESSING);
		}
	}
	
	/**
	 * Abandon payment, but keeps invoices for later processing.
	 * 
	 * @return abandon payment message block
	 */
	public void abandonPaymentKeepInvoice() {
		if (!isPaymentProcessed() && !isProcessingPayment() && !isPaymentCanceled(payment)) {
			synchronized (context) {
				try {
					changeCurrentState(PaymentStates.IN_PROCESSING);
					payment.abandonPaymentKeepInvoice();
					payment.getObjectContext().commitChanges();
				} finally {
					changeCurrentState(PaymentStates.PROCESSED);
				}
			}
		} else {
			changeCurrentState(PaymentStates.IN_PROCESSING);
		}
	}
	
	/**
	 * Marks current trasaction as finalized, creates a new one for other credit
	 * card details.
	 * 
	 * @return payment form component
	 */
	public void tryOtherCard() {
		synchronized (context) {
			if (!isPaymentProcessed() && !isProcessingPayment() && !isPaymentCanceled(payment) && !PaymentResult.isNewPayment(payment)) {
				this.payment = payment.makeCopy();
				this.payment.setStatus(PaymentStatus.IN_TRANSACTION);
				this.payment.getObjectContext().commitChanges();
				changeCurrentState(PaymentStates.NOT_PROCESSED);
			} else {
				if (PaymentResult.isNewPayment(payment)) {
					changeCurrentState(PaymentStates.NOT_PROCESSED);
				} else {
					changeCurrentState(PaymentStates.IN_PROCESSING);
				}
			}
		}
	}
	
	public boolean isPaymentProcessed() {
    	return PaymentResult.isSuccessPayment(payment);
    }
	
	public static boolean isPaymentCanceled(final PaymentIn paymentIn) {
		if (!PaymentStatus.STATUSES_FAILED.contains(paymentIn.getStatus())) {
			return false;
		} else {
			for (PaymentInLine paymentLine : paymentIn.getPaymentInLines()) {
				for (InvoiceLine invoiceLine : paymentLine.getInvoice().getInvoiceLines()) {
					if (invoiceLine.getEnrolment() != null) {
						if (EnrolmentStatus.FAILED.equals(invoiceLine.getEnrolment().getStatus()) 
								|| EnrolmentStatus.SUCCESS.equals(invoiceLine.getEnrolment().getStatus())) {
							return true;
						} else {
							return false;
						}
					}
				}
			}
			return true;
		}
	}
	
	public boolean isShowResult() {
		Session session = request.getSession(false);
		if (session != null) {
			return PaymentStates.PROCESSED.equals(currentState);
		}
		return false;
	}
	
	public boolean isProcessingPayment() {
		Session session = request.getSession(false);
		if (session != null) {
			return PaymentStates.IN_PROCESSING.equals(currentState);
		}
		return false;
	}
	
	public boolean isProcessigComplete() {
		boolean result = currentProcessFeature.isCancelled() || currentProcessFeature.isDone() || !PaymentResult.isNewPayment(payment);
		if (currentProcessFeature.isCancelled() && PaymentResult.isNewPayment(payment)) {
			//this should be the emergency state that processing thread is canceled.
			payment.failPayment();
		}
		if (result) {
			changeCurrentState(PaymentStates.PROCESSED);
		}
		return result;
	}
	
	public boolean isShowPaymentForm() {
		Session session = request.getSession(false);
		if (session != null) {
			return PaymentStates.NOT_PROCESSED.equals(currentState);
		}
		return false;
	}
	
	private class ProcessPaymentInvokable implements Invokable<PaymentIn> {
		private final PaymentIn payment;
		private final IPaymentGatewayService paymentGatewayService;
		
		private ProcessPaymentInvokable(PaymentIn payment, IPaymentGatewayService paymentGatewayService) {
			this.payment = payment;
			this.paymentGatewayService = paymentGatewayService;
		}

		@Override
		public PaymentIn invoke() {
			synchronized (payment.getObjectContext()) {
				if (PaymentResult.isNewPayment(payment)) {
					paymentGatewayService.performGatewayOperation(payment);
					if (PaymentResult.isSuccessPayment(payment)) {
						payment.getObjectContext().commitChanges();
					}
				}
			}
			return payment;
		}
	}
	
	public class AbandonStackedPaymentInvokable implements Invokable<Boolean> {
		public static final long SLEEP_TIME = 3 * 1000;//3 second
		public static final long TIMEOUT = 10 * 60 * 1000;//10 minutes
		private boolean isCanceled;
		private long invokedTime;
		private boolean isProcessed;
		private final PaymentIn payment;
		
		public AbandonStackedPaymentInvokable(long invokedTime, PaymentIn payment) {
			isCanceled = false;
			this.invokedTime = invokedTime;
			isProcessed = false;
			this.payment = payment;
		}
		
		/**
		 * @return the isProcessed
		 */
		public boolean isProcessed() {
			return isProcessed;
		}

		/**
		 * @param isCanceled the isCanceled to set
		 */
		public void setCanceled(boolean isCanceled) {
			this.isCanceled = isCanceled;
		}
		
		/**
		 * @return the isCanceled
		 */
		public boolean isCanceled() {
			return isCanceled;
		}

		/**
		 * @return the invokedTime
		 */
		public long getInvokedTime() {
			return invokedTime;
		}
		
		/**
		 * @return the timeout
		 */
		public long getTimeout() {
			return TIMEOUT;
		}

		@Override
		public Boolean invoke() {
			while (!isCanceled() && (new Date().getTime() - invokedTime <= getTimeout())) {
				try {
					LOGGER.info("Wait abandon till timeout. Seconds left = " + (getTimeout() - (new Date().getTime() - invokedTime))/1000);
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					LOGGER.error("Abandon stack payment watchdog was interrupted", e);
					isCanceled = true;
					isProcessed = true;
					return false;
				}
			}
			if (!isCanceled()) {
				//if abandon not re-scheduled yet we need to call it
				if (!PaymentResult.isSuccessPayment(payment) && !isPaymentCanceled(payment)) {
					synchronized (payment.getObjectContext()) {
						PaymentInAbandonUtil.abandonPaymentReverseInvoice(payment);
					}
				}
			}
			isProcessed = true;
			return true;
		}
		
	}
	
	private enum PaymentStates {
		NOT_PROCESSED,IN_PROCESSING, PROCESSED;
	}
}
