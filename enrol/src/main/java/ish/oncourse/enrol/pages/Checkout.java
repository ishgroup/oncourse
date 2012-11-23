package ish.oncourse.enrol.pages;

import ish.math.Money;
import ish.oncourse.enrol.checkout.HTMLUtils;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.checkout.PurchaseController.Action;
import ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;
import ish.oncourse.enrol.checkout.PurchaseModel;
import ish.oncourse.enrol.services.payment.IPurchaseControllerBuilder;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.ui.pages.Courses;
import ish.oncourse.util.FormatUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.Format;

public class Checkout {
	public static final String DATE_FIELD_FORMAT = "MM/dd/yyyy";

	public static final Logger LOGGER = Logger.getLogger(Checkout.class);

	@Inject
	private ICookiesService cookiesService;

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
	private Messages messages;

	@Persist
	private PurchaseController purchaseController;


	@Inject
	@Id("checkout")
	private Block checkoutBlock;

	@Inject
	@Id("concession")
	@Property
	private Block blockConcession;

	@Property
	private String error;


	/**
	 * The property is true when session for the payment was expired.
	 */
	private boolean expired;

	@Persist
	private Throwable unexpectedThrowable;

	@SetupRender
	void beforeRender() {
		synchronized (this) {
			if (unexpectedThrowable != null) {
				handleUnexpectedThrowable();
			}

			initPaymentController();
		}
	}

	private void handleUnexpectedThrowable() {
		IllegalArgumentException exception = new IllegalArgumentException(unexpectedThrowable);
		if (purchaseController != null) {
			purchaseController.getModel().getObjectContext().rollbackChanges();
			resetPersistProperties();
		}
		throw exception;
	}


	@AfterRender
	public void afterRender() {
		if (purchaseController != null && purchaseController.isFinished()) {
			resetPersistProperties();
		}
	}

	public PurchaseController getPurchaseController() {
		return purchaseController;
	}

	public void resetPersistProperties()
	{
		expired = false;
		unexpectedThrowable = null;
		purchaseController = null;
	}

	private void initPaymentController() {
		if (purchaseController == null) {
			PurchaseModel model = purchaseControllerBuilder.build();
			purchaseController = purchaseControllerBuilder.build(model);
			purchaseController.performAction(new ActionParameter(Action.init));
		}
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
		return HTMLUtils.getUrlBy(request, Courses.class);
	}

	@OnEvent(value = "addContactEvent")
	public Object addContact() {
		if (!request.isXHR())
			return null;
		purchaseController.performAction(new ActionParameter(Action.startAddContact));
		return checkoutBlock;
	}

	@OnEvent(value = "proceedToPaymentEvent")
	public Object proceedToPayment() {
		if (!request.isXHR())
			return null;
		ActionParameter actionParameter = new ActionParameter(Action.proceedToPayment);
		actionParameter.setValue(purchaseController.getModel().getPayment());
		purchaseController.performAction(actionParameter);
		return checkoutBlock;
	}

	public Object onException(Throwable cause) {
		if (purchaseController != null) {
			unexpectedThrowable = cause;
		} else {
			expired = true;
			LOGGER.warn("Persist properties have been cleared. User used two or more tabs or session was expired", cause);
		}
		return checkoutBlock;
	}

	public boolean isExpired() {
		return expired;
	}

	public Throwable getUnexpectedThrowable() {
		return unexpectedThrowable;
	}

	public Format moneyFormat(Money money)
	{
		return FormatUtils.chooseMoneyFormat(money);
	}
}
