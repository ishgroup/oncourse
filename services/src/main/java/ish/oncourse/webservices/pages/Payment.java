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
import ish.oncourse.webservices.components.PaymentForm;
import ish.oncourse.webservices.exception.PaymentNotFoundException;
import ish.oncourse.webservices.utils.PaymentInAbandonUtil;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.log4j.Logger;
import org.apache.tapestry5.Block;
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
					
	/**
	 * Finds and init payment and payment transaciton by referenceId.
	 * 
	 * @param sessionId
	 */
	void onActivate(String sessionId) {
			this.payment = paymentService.currentPaymentInBySessionId(sessionId);
			if (this.payment == null) {
				throw new PaymentNotFoundException(messages.format("payment.not.found", sessionId));
			}
			this.moneyFormat = new DecimalFormat(PAYMENT_AMOUNT_FORMAT);
			Session session = request.getSession(true);
			session.setAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE, payment.getCollege().getId());
			this.totalIncGst = new Money(payment.getAmount());
			this.payer = this.payment.getContact();
			initInvoices();
	}

	private void initInvoices() {
		this.invoices = new ArrayList<Invoice>();		
		for (PaymentInLine paymentLine : this.payment.getPaymentInLines()) {
			this.invoices.add(paymentLine.getInvoice());
		}		
		Ordering ordering = new Ordering(Invoice.INVOICE_NUMBER_PROPERTY, SortOrder.ASCENDING);
		ordering.orderList(this.invoices);
	}

	public boolean isPaymentBeingProcessed() {
		Session session = request.getSession(false);
		if (session == null) {
			return false;
		} else {
			Boolean processed = (Boolean) session.getAttribute(PaymentIn.PAYMENT_PROCESSED_PARAM);
			return processed != null && processed;
		}
	}

	public boolean isShowPaymentResult() {
		return payment.getStatus() != PaymentStatus.IN_TRANSACTION && payment.getStatus() != PaymentStatus.CARD_DETAILS_REQUIRED;
	}

	public String getPaymentTitle() {
		return messages.format("payment.title", payer.getGivenName(), payer.getFamilyName());
	}

	public String getInvoiceDescription() {
		return messages.format("invoice.desc", invoice.getInvoiceNumber());
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
		this.payment = payment.makeCopy();
		this.payment.setStatus(PaymentStatus.IN_TRANSACTION);
		this.payment.getObjectContext().commitChanges();
		return paymentForm;
	}

	/**
	 * Abandon payment, reverses invoices
	 * 
	 * @return abandon payment message block
	 */
	public Object abandonPaymentReverseInvoice() {
		final Session session = request.getSession(true);
		if (!isPaymentProcessed() && !Boolean.TRUE.equals(session.getAttribute(PaymentIn.PAYMENT_PROCESSED_PARAM)) && !isPaymentCanceled(payment)) {
			try {
				session.setAttribute(PaymentIn.PAYMENT_PROCESSED_PARAM, Boolean.TRUE);
				PaymentInAbandonUtil.abandonPaymentReverseInvoice(payment);
			} finally {
				session.setAttribute(PaymentIn.PAYMENT_PROCESSED_PARAM, null);
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
		final Session session = request.getSession(true);
		if (!isPaymentProcessed() && !Boolean.TRUE.equals(session.getAttribute(PaymentIn.PAYMENT_PROCESSED_PARAM)) && !isPaymentCanceled(payment)) {
			try {
				session.setAttribute(PaymentIn.PAYMENT_PROCESSED_PARAM, Boolean.TRUE);
				payment.abandonPaymentKeepInvoice();
				payment.getObjectContext().commitChanges();
			} finally {
				session.setAttribute(PaymentIn.PAYMENT_PROCESSED_PARAM, null);
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
    	return PaymentStatus.SUCCESS.equals(payment.getStatus());
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
    	if (isPaymentProcessed() || isPaymentCanceled(payment)) {
			LOGGER.warn("Payment were processed. User used two or more tabs", cause);
			return this;
		} else {
			throw new IllegalArgumentException(cause);
		}
    }
	
	
}
