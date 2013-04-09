package ish.oncourse.enrol.pages;

import ish.math.Money;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.checkout.PurchaseController.Action;
import ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;
import ish.oncourse.enrol.checkout.PurchaseModel;
import ish.oncourse.enrol.services.payment.IPurchaseControllerBuilder;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.ui.pages.Courses;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.HTMLUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;

import java.text.Format;

public class Checkout {
    /**
     * the format is used to parse string value which an user puts in date field.
     * It uses "yy" format for year because the the format parses years like 11, 73, 85 correctly. For example:
     * if an user enters 1/1/73 it means 01/01/1973 but not 01/01/0073 which it would be got when it uses format yyyy
     */
    public static final String DATE_FIELD_PARSE_FORMAT = "dd/MM/yy";

    public static final String DATE_FIELD_SHOW_FORMAT = "dd/MM/yyyy";


    public static final Logger LOGGER = Logger.getLogger(Checkout.class);

    @Inject
    private ICookiesService cookiesService;

    @Inject
    private IStudentService studentService;

    @Inject
    private ICourseClassService courseClassService;

    @Inject
    private PreferenceController preferenceController;

    @Inject
    private IVoucherService voucherService;

    @Inject
    private ITagService tagService;

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private IWebSiteService webSiteService;

    @Inject
    private IPurchaseControllerBuilder purchaseControllerBuilder;

    @Inject
    private Request request;

    @Inject
    private Response response;

    @Inject
    private Messages messages;

    @SessionState(create = false)
    private PurchaseController purchaseController;


    @Inject
    @Id("checkout")
    private Block checkoutBlock;

    @Inject
    @Id("concession")
    @Property
    private Block blockConcession;


    @InjectPage
    private Payment paymentPage;

    /**
     * The property is true when session for the payment was expired.
     */
    @Property
    @Persist
    private boolean expired;


    Object onActivate() {
        if (expired)
            return Payment.class.getSimpleName();

        synchronized (this) {
            if (purchaseController == null) {
                purchaseController = buildPaymentController();
                if (purchaseController.isPaymentResult())
                    //redirect if init data is not correct , for example: course classes are not selected
                    return Payment.class.getSimpleName();
            } else if (purchaseController.isPaymentState() && !purchaseController.adjustState(Action.enableEnrolment)) {
                //browser back-button handle
                return Payment.class.getSimpleName();
            } else if (purchaseController.isEditCheckout())
                //add new items from shopping basket
                updatePurchaseItems();
        }
        return null;

    }

    @SetupRender
    void setupRender() {
    }

    public synchronized PurchaseController getPurchaseController() {
        return purchaseController;
    }

    public void resetCookies() {
        cookiesService.writeCookieValue(CourseClass.SHORTLIST_COOKIE_KEY, StringUtils.EMPTY);
        cookiesService.writeCookieValue(Discount.PROMOTIONS_KEY, StringUtils.EMPTY);
        studentService.clearStudentsShortList();
    }

    public void resetPersistProperties() {
        expired = false;
        purchaseController = null;
    }

    private PurchaseController buildPaymentController() {
        PurchaseModel model = purchaseControllerBuilder.build();
        PurchaseController purchaseController = purchaseControllerBuilder.build(model);
        purchaseController.performAction(new ActionParameter(Action.init));
        return purchaseController;
    }

    /**
     * Checks if the payment gateway processing is enabled for the current
     * college. If not, the enrolling is impossible.
     *
     * @return true if payment gateway is enabled.
     */
    public boolean isPaymentGatewayEnabled() {
        return preferenceController.isPaymentGatewayEnabled();
    }


    public Block getCheckoutBlock() {
        return checkoutBlock;
    }

    public String getCoursesLink() {
        return HTMLUtils.getUrlBy(request.getServerName(), Courses.class);
    }

    @OnEvent(value = "addContactEvent")
    public Object addContact() {
        if (!request.isXHR())
            return null;
        purchaseController.performAction(new ActionParameter(Action.addContact));
        return checkoutBlock;
    }

    @OnEvent(value = "proceedToPaymentEvent")
    public Object proceedToPayment() {
        ActionParameter actionParameter = new ActionParameter(Action.proceedToPayment);
        actionParameter.setValue(purchaseController.getModel().getPayment());
        purchaseController.performAction(actionParameter);
        if (purchaseController.getErrors().isEmpty())
            return paymentPage;
        else
            return this;
    }

    public void updatePurchaseItems() {
        if (purchaseController != null) {
            purchaseControllerBuilder.updatePurchaseItems(purchaseController);
            purchaseController.updateTotalIncGst();
            purchaseController.updateTotalDiscountAmountIncTax();
        }
    }

    public Object onException(Throwable cause) {
        if (purchaseController == null) {
            LOGGER.warn("", cause);
            expired = true;
        } else {
            expired = false;
            purchaseController = null;
            throw new IllegalArgumentException(cause);
        }
        return paymentPage;
    }

    public boolean isExpired() {
        return expired;
    }

    public Format moneyFormat(Money money) {
        return FormatUtils.chooseMoneyFormat(money);
    }

    public void finalizingProcess() {
        //when the process if finished we should reset all persists properties to allow the next purchase process
        if (purchaseController != null && purchaseController.isFinished()) {
            if (purchaseController.getPaymentEditorDelegate() != null &&
                    purchaseController.getPaymentEditorDelegate().isPaymentSuccess())
                resetCookies();
            resetPersistProperties();
        }
    }
}
