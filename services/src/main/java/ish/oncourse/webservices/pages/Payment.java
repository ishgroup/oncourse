package ish.oncourse.webservices.pages;

import ish.math.Money;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayServiceBuilder;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.util.payment.PaymentProcessController;
import ish.oncourse.util.payment.PaymentProcessController.PaymentAction;
import ish.oncourse.webservices.components.PaymentForm;
import ish.oncourse.webservices.exception.PaymentNotFoundException;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.ParallelExecutor;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.List;

import static ish.oncourse.util.payment.PaymentProcessController.PaymentAction.UPDATE_PAYMENT_GATEWAY_STATUS;

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

    @Inject
    private IPaymentService paymentService;

    @Inject
    private ComponentResources componentResources;

	@Inject
	private ICayenneService cayenneService;

    @InjectComponent
    private PaymentForm paymentForm;

    @Inject
    private IPaymentGatewayServiceBuilder paymentGatewayServiceBuilder;

    @Inject
    private ParallelExecutor parallelExecutor;

    @Property
    private Contact payer;

    @Property
    private Money totalIncGst;

    @Property
    private Format moneyFormat;

    @Property
    private Invoice invoice;

    @Property
    private List<Invoice> invoices;

    @Persist
    private PaymentProcessController paymentProcessController;

    /**
     * Clears all the properties with the @Persist annotation.
     */
    public void clearPersistedProperties() {
        LOGGER.info("Persisted Properties are being cleared");
        componentResources.discardPersistentFieldChanges();
    }

    public URL getPageURL() {
        try {
            return new URL(getRefreshLink());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Finds and init payment and payment transaciton by referenceId.
     *
     * @param sessionId
     */
    void onActivate(String sessionId) {
        synchronized (this) {
            if (paymentProcessController == null) {
                PaymentIn paymentIn = paymentService.currentPaymentInBySessionId(sessionId);
                if (paymentIn == null) {
                    throw  new PaymentNotFoundException(messages.format("payment.not.found", sessionId));
                }

                Session session = request.getSession(true);
                //need for WebSiteService
                session.setAttribute(College.REQUESTING_COLLEGE_ATTRIBUTE, paymentIn.getCollege().getId());

                paymentProcessController = new PaymentProcessController();
                paymentProcessController.setObjectContext(cayenneService.newContext());
                paymentProcessController.setParallelExecutor(parallelExecutor);
                paymentProcessController.setPaymentGatewayService(paymentGatewayServiceBuilder.buildService());
				paymentProcessController.setCayenneService(cayenneService);
                paymentProcessController.setPaymentIn(paymentIn);
                paymentProcessController.processAction(PaymentAction.INIT_PAYMENT);
            }

            this.moneyFormat = new DecimalFormat(PAYMENT_AMOUNT_FORMAT);
            this.totalIncGst = new Money(paymentProcessController.getAmount());
            this.payer = paymentProcessController.getContact();
            this.invoices = paymentProcessController.getInvoices();
        }
        if (paymentProcessController.getCurrentState() == PaymentProcessController.PaymentProcessState.PROCESSING_PAYMENT)
        {
            paymentProcessController.processAction(UPDATE_PAYMENT_GATEWAY_STATUS);
        }
    }

    /**
     * Returns true if user tried to use a few tabs
     * @return
     */
    public boolean isIllegalState()
    {
        return paymentProcessController.isIllegalState();
    }

	public boolean isExpired()
	{
		return paymentProcessController.isExpired();
	}


	@AfterRender
    public void afterRender()
    {
        if (paymentProcessController.isProcessFinished())
        {
            clearPersistedProperties();
        }
    }

    public String getPaymentTitle() {
        return messages.format("payment.title", payer.getFullName());
    }

    public String getInvoiceDescription() {
        return messages.format("invoice.desc", invoice.getInvoiceNumber());
    }

    public Object handleThrowable(Throwable cause) {
        paymentProcessController.setThrowable(cause);
        return getPageURL();
    }

    public String getRefreshLink() {
        return Payment.HTTPS_PROTOCOL + request.getServerName() + request.getContextPath() + Payment.PAYMENT_PAGE_NAME + paymentProcessController.getPaymentIn().getSessionId();
    }

    public PaymentProcessController getPaymentProcessController() {
        return paymentProcessController;
    }

    public boolean isShowForm() {
        return paymentProcessController.getCurrentState() == PaymentProcessController.PaymentProcessState.FILL_PAYMENT_DETAILS;
    }

    public boolean isShowResult() {
        return   paymentProcessController.isFinalState();
    }

    public boolean isShowProcessing() {
        return paymentProcessController.isProcessingState();
    }
}
