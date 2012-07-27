package ish.oncourse.webservices.pages;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.webservices.components.PaymentForm;
import ish.oncourse.webservices.components.PaymentProcessing;
import ish.oncourse.webservices.components.PaymentResult;
import ish.oncourse.webservices.exception.PaymentNotFoundException;
import ish.oncourse.webservices.utils.PaymentInAbandonUtil;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

@Import(stylesheet = "css/screen.css")
public class Payment {
	 private static final Logger LOGGER = Logger.getLogger(Payment.class);
	private static final String PAYMENT_AMOUNT_FORMAT = "###,##0.00";

	@Inject
	private IPaymentService paymentService;

	@Inject
	private Messages messages;

	@Inject
	private Request request;

	@Property
	@Persist
	private PaymentIn payment;

	@SuppressWarnings("all")
	@Property
	private Money totalIncGst;

	@SuppressWarnings("all")
	@Property
	private String enrolmentDisclosure;

	@Property
	private Contact payer;

	@SuppressWarnings("all")
	@Property
	private Format moneyFormat;

	@SuppressWarnings("all")
	@InjectComponent
	private Zone paymentZone;

	@Property
	private List<Invoice> invoices;

	@Property
	private Invoice invoice;

	@Inject
	private Block cancelledMessageBlock;

	@SuppressWarnings("all")
	@Property
	@Inject
	private Block paymentResultBlock;

	@Property
	@Inject
	private Block progressDisplayBlock;

	@InjectComponent
	private PaymentForm paymentForm;
	
	@Persist
    private ObjectContext context;
	
    @Inject
    private ICayenneService cayenneService;
    
    @Inject
    private ComponentResources componentResources;
    
    @SuppressWarnings("all")
	@InjectComponent
    @Property
    private PaymentProcessing resultComponent;
    
    @Inject
	private IPaymentGatewayService paymentGatewayService;
    
    @SuppressWarnings("all")
	@InjectComponent
    private PaymentResult result;
    
    /**
     * Indicates if this page is used for displaying the payment checkout(if
     * false), and the result of previous checkout otherwise.
     */
    @Persist
    private boolean checkoutResult;
    
    /*public PaymentProcessing getResultingElement() {
        return resultComponent;
    }*/
    
    /**
     * Sets value to the {@link #checkoutResult}.
     *
     * @param checkoutResult .
     */
    public void setCheckoutResult(boolean checkoutResult) {
        this.checkoutResult = checkoutResult;
    }

    /**
     * @return the checkoutResult
     */
    public boolean isCheckoutResult() {
        return checkoutResult;
    }
        
    /**
     * Clears all the properties with the @Persist annotation.
     */
    public void clearPersistedProperties() {
        componentResources.discardPersistentFieldChanges();
    }
	
	public ObjectContext getContext() {
        return context;
    }

    /**
     * Method returns true if by some reason persist properties have been cleared.
     * For example: the payment has been processed from other tab of the browser.
     */
    public boolean isPersistCleared() {
        return context == null;
    }
    
    private void checkContext() {
    	synchronized (this) {
            if (context == null) {
                context = cayenneService.newContext();
            }
        }
    }
					
	/**
	 * Finds and init payment and payment transaciton by referenceId.
	 * 
	 * @param sessionId
	 */
	void onActivate(String sessionId) {
		checkContext();
		synchronized (context) {
			this.payment = paymentService.currentPaymentInBySessionId(sessionId);
			if (payment == null) {
				clearPersistedProperties();
				throw new PaymentNotFoundException(messages.format("payment.not.found", sessionId));
			} else {
				payment = (PaymentIn) context.localObject(payment.getObjectId(), null);
			}
			this.moneyFormat = new DecimalFormat(PAYMENT_AMOUNT_FORMAT);
			Session session = request.getSession(true);
			session.setAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE, payment.getCollege().getId());
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

	/*public boolean isPaymentBeingProcessed() {
		Session session = request.getSession(false);
		if (session == null) {
			return false;
		} else {
			Boolean processed = (Boolean) session.getAttribute(PaymentIn.PAYMENT_PROCESSED_PARAM);
			return processed != null && processed;
		}
	}*/

	public boolean isShowPaymentResult() {
		return !PaymentResult.isNewPayment(payment);
	}

	public String getPaymentTitle() {
		return messages.format("payment.title", payer.getGivenName(), payer.getFamilyName());
	}

	public String getInvoiceDescription() {
		return messages.format("invoice.desc", invoice.getInvoiceNumber());
	}
	
	public void processPayment() {
		synchronized (context) {
			if (isCheckoutResult()) {
				if (PaymentResult.isNewPayment(payment)) {
					// additional check for card details required added to avoid #13172
					paymentGatewayService.performGatewayOperation(payment);
					if (PaymentResult.isSuccessPayment(payment)) {
						payment.getObjectContext().commitChanges();
					}
				}
			
			}
		}
	}

	/**
	 * Submits task for for calling payment gateway to transaction executor.
	 */
	public Object submitToGateway() {
		return progressDisplayBlock;
	}

	/**
	 * Marks current trasaction as finalized, creates a new one for other credit
	 * card details.
	 * 
	 * @return payment form component
	 */
	public Object tryOtherCard() {
		synchronized (context) {
			if (!isPaymentProcessed() && !isCheckoutResult() && !isPaymentCanceled(payment) && !PaymentResult.isNewPayment(payment)) {
				this.payment = payment.makeCopy();
				this.payment.setStatus(PaymentStatus.IN_TRANSACTION);
				this.payment.getObjectContext().commitChanges();
				return paymentForm;
			} else {
				if (PaymentResult.isNewPayment(payment)) {
					return paymentForm;
				} else {
					return progressDisplayBlock;
				}
			}
		}
	}

	/**
	 * Abandon payment, reverses invoices
	 * 
	 * @return abandon payment message block
	 */
	public Object abandonPaymentReverseInvoice() {
		if (!isCheckoutResult() && !isPaymentProcessed() && !isPaymentCanceled(payment)) {
			synchronized (context) {
				try {
					setCheckoutResult(true);
					PaymentInAbandonUtil.abandonPaymentReverseInvoice(payment);
				} finally {
					setCheckoutResult(false);
				}
			}
			return cancelledMessageBlock;
		} else {
			return progressDisplayBlock;
		}
	}

	/**
	 * Abandon payment, but keeps invoices for later processing.
	 * 
	 * @return abandon payment message block
	 */
	public Object abandonPaymentKeepInvoice() {
		if (!isPaymentProcessed() && !isCheckoutResult() && !isPaymentCanceled(payment)) {
			synchronized (context) {
				try {
					setCheckoutResult(true);
					payment.abandonPaymentKeepInvoice();
					payment.getObjectContext().commitChanges();
				} finally {
					setCheckoutResult(false);
				}
			}
			return cancelledMessageBlock;
		} else {
			return progressDisplayBlock;
		}
	}

	/**
	 * Cancels payment, sends notification, displays cancelled message.
	 * 
	 * @return cancelled message block
	 */
	public Object cancelPayment() {
		return abandonPaymentReverseInvoice();
	}
    
	public boolean isPaymentProcessed() {
    	return PaymentResult.isSuccessPayment(payment);
    }
	
	public static boolean isPaymentCanceled(final PaymentIn paymentIn) {
		if (!PaymentStatus.FAILED.equals(paymentIn.getStatus())) {
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
    
    public Object handleUnexpectedException(final Throwable cause) {
    	if (isPersistCleared()) {
			LOGGER.warn("Payment were processed. User used two or more tabs", cause);
			return this;
		} else {
			throw new IllegalArgumentException(cause);
		}
    }
	
	
}
