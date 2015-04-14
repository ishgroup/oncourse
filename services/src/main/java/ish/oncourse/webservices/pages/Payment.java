package ish.oncourse.webservices.pages;

import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayServiceBuilder;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.util.payment.PaymentProcessController;
import ish.oncourse.util.payment.PaymentProcessControllerBuilder;
import ish.oncourse.webservices.components.PaymentForm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.ParallelExecutor;
import org.apache.tapestry5.services.Request;

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
    private static final Logger logger = LogManager.getLogger();
    private static final String PAYMENT_AMOUNT_FORMAT = "###,##0.00";
    public static final String HTTPS_PROTOCOL = "https://";


    @Inject
    private Messages messages;

    @Inject
    private Request request;

    @Inject
    private ComponentResources componentResources;

    @InjectComponent
    private PaymentForm paymentForm;

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

    @Inject
    private IPaymentService paymentService;

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private IPaymentGatewayServiceBuilder paymentGatewayServiceBuilder;

    @Inject
    private ParallelExecutor parallelExecutor;

    @Property
    private String errorMessage;

    /**
     * Clears all the properties with the @Persist annotation.
     */
    public void clearPersistedProperties() {
        logger.info("Persisted Properties are being cleared");
        componentResources.discardPersistentFieldChanges();
    }

    public URL getPageURL() {
        try {
            return new URL(getRefreshLink());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void resetOldSessionController(String sessionId) {
        if (PaymentProcessControllerBuilder.isNeedResetOldSessionController(getPaymentProcessController(), sessionId)) {
            //reset the paymentProcessController to be able render actual payment
            paymentProcessController = null;
        }
    }

    /**
     * Finds and init payment and payment transaciton by referenceId.
     *
     * @param sessionId
     */
    void onActivate(String sessionId) {
        synchronized (this) {
            //firstly check that there is no controller with expired session
            resetOldSessionController(sessionId);
            if (paymentProcessController == null) {
                PaymentIn payment = validateSessionId(sessionId);
                if (payment != null)
                {
                    paymentProcessController = new PaymentProcessControllerBuilder(parallelExecutor, paymentGatewayServiceBuilder, cayenneService, paymentService,
                            request.getSession(true)).build(payment);
                    initProperties();
                }
            } else {
                if (paymentProcessController.getCurrentState() == PaymentProcessController.PaymentProcessState.PROCESSING_PAYMENT) {
                    paymentProcessController.processAction(UPDATE_PAYMENT_GATEWAY_STATUS);
                }
                initProperties();
            }
        }
    }

    private PaymentIn validateSessionId(String sessionId) {
        List<PaymentIn> payments = paymentService.getPaymentsBySessionId(sessionId);
        if (payments.size() == 1 && payments.get(0).getStatus() == PaymentStatus.IN_TRANSACTION) {
            return payments.get(0);
        }

        if (payments.size() == 0) {
            errorMessage = messages.format("payment.not.found", sessionId);
            logger.error(errorMessage);
            return null;
        }

        for (PaymentIn paymentIn : payments) {
            if (paymentIn.getStatus() == PaymentStatus.CARD_DETAILS_REQUIRED) {
                errorMessage = messages.format("payment.already.processed", sessionId);
                logger.warn("collegeId: {}, {}", payments.get(0).getCollege().getId(), errorMessage);
                return null;
            }
        }

        errorMessage = messages.format("payment.has.finalstatus", sessionId);
        logger.warn("collegeId: {}, {}", payments.get(0).getCollege().getId(), errorMessage);
        return null;
    }


    private void initProperties() {
        this.moneyFormat = new DecimalFormat(PAYMENT_AMOUNT_FORMAT);
        this.totalIncGst = paymentProcessController.getAmount();
        this.payer = paymentProcessController.getContact();
        this.invoices = paymentProcessController.getInvoices();
    }


    /**
     * Returns true if user tried to use a few tabs
     *
     * @return
     */
    public boolean isIllegalState() {
        return paymentProcessController.isIllegalState();
    }

    public boolean isExpired() {
        return paymentProcessController.isExpired();
    }


    @AfterRender
    public void afterRender() {
        if (paymentProcessController != null && paymentProcessController.isProcessFinished()) {
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
        return paymentProcessController.isFinalState();
    }

    public boolean isShowProcessing() {
        return paymentProcessController != null && paymentProcessController.isProcessingState();
    }
}
