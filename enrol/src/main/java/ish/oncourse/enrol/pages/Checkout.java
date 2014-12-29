package ish.oncourse.enrol.pages;

import ish.math.Money;
import ish.oncourse.enrol.checkout.ActionConfirmApplication;
import ish.oncourse.enrol.checkout.ActionEnableApplication;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.checkout.PurchaseController.Action;
import ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;
import ish.oncourse.enrol.checkout.PurchaseModel;
import ish.oncourse.enrol.services.PageExceptionHandler;
import ish.oncourse.enrol.services.payment.IPurchaseControllerBuilder;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.Product;
import ish.oncourse.services.contact.IContactService;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.ui.components.internal.PageStructure;
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

@Secure // this anatation is important. The page should use secure handling allways
public class Checkout {

    public static final Logger LOGGER = Logger.getLogger(Checkout.class);

    @Inject
    private ICookiesService cookiesService;
	
	@Inject
	private IContactService contactService;

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
    @Property
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

    @InjectComponent
    private PageStructure pageStructure;

    /**
     * The property is true when session for the payment was expired.
     */
    @Property
    @Persist
    private boolean expired;


    Object onActivate() {

		if (isExpired())
			return Payment.class.getSimpleName();

		/**
		 * The check was added to handle expire session for ajax requests correctly.
		 */
		if (request.isXHR())
			return null;

		if (isInitRequest()) {
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
        pageStructure.setCart(purchaseController.getCart());
        return null;

    }

	private boolean isInitRequest() {
		return purchaseController == null && request.getPath().toLowerCase().equals("/checkout");
	}

    public PurchaseController getPurchaseController() {
        return purchaseController;
    }

    public void resetCookies() {
		cookiesService.writeCookieValue(Product.SHORTLIST_COOKIE_KEY, StringUtils.EMPTY);
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

		ActionParameter parameter = new ActionParameter(Action.init);
		String uniqCode = cookiesService.getCookieValue(Contact.STUDENT_PROPERTY);
		if (StringUtils.trimToNull(uniqCode) != null) {
			Contact contact = contactService.findByUniqueCode(uniqCode);
			if (contact != null) {
				parameter.setValue(contact);
			}
		}
        purchaseController.performAction(parameter);
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

		PageExceptionHandler handler = new PageExceptionHandler();
		handler.setPurchaseController(purchaseController);
		handler.setCause(cause);
		handler.setRequest(request);
		handler.handle();

		expired = handler.isExpired();

		if (handler.isUnexpected()) {
			purchaseController = null;
            LOGGER.error(cause.getMessage(), cause);
			throw new IllegalArgumentException(cause);
		}


		if (handler.isRedirect()) {
			return redirect();
		} else {
			/**
			 * the code renders only a defined block (not whole page) for ajax requests.
			 */
			if (request.isXHR()) {
				return paymentPage.getPaymentBlock();
			} else {
				return paymentPage;
			}
		}
	}

	private Object redirect() {
		if (purchaseController.isPaymentState()) {
			if (request.isXHR()) {
				return paymentPage.getPaymentBlock();
			} else {
				return paymentPage;
			}
		} else {
			if (request.isXHR()) {
				return checkoutBlock;
			} else {
				return this;
			}
		}
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

	public Payment getPaymentPage() {
		return paymentPage;
	}
}
