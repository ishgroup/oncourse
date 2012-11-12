package ish.oncourse.enrol.pages;

import ish.common.types.CreditCardType;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.checkout.PurchaseController.Action;
import ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;
import ish.oncourse.enrol.checkout.PurchaseModel;
import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.enrol.checkout.payment.PaymentEditorDelegate;
import ish.oncourse.enrol.services.payment.IPurchaseControllerBuilder;
import ish.oncourse.enrol.utils.EnrolCoursesController;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Product;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.ui.pages.Courses;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Arrays;
import java.util.List;

@Import(library={"context:js/jquery.blockUI.js","context:js/checkout.js"},
		stylesheet="context:css/checkout.css")

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
	@Property
	private boolean expired;

	@SetupRender
	void beforeRender() {
		synchronized (this) {
			initPaymentController();
		}
		expired = false;
	}

	@AfterRender
	public void afterRender()
	{
		if (purchaseController != null && purchaseController.isFinished())
		{
			purchaseController = null;
		}
	}

	public PurchaseController getPurchaseController() {
		return purchaseController;
	}

	private void initPaymentController() {
		if (purchaseController == null) {
			List<Long> orderedClassesIds = cookiesService.getCookieCollectionValue(CourseClass.SHORTLIST_COOKIE_KEY, Long.class);
			List<Long> productIds = cookiesService.getCookieCollectionValue(Product.SHORTLIST_COOKIE_KEY, Long.class);
			List<CourseClass> courseClasses = courseClassService.loadByIds(orderedClassesIds);
			List<Product> products = voucherService.loadByIds(productIds);

			PurchaseModel model = new PurchaseModel();
			model.setObjectContext(cayenneService.newContext());
			model.setClasses(model.localizeObjects(courseClasses));
			model.setProducts(model.localizeObjects(products));
			model.setCollege(model.localizeObject(webSiteService.getCurrentCollege()));

			purchaseController = purchaseControllerBuilder.build(model);
			purchaseController.performAction(new ActionParameter(Action.init));
		}
	}

	private void initTestPaymentController() {
		if (purchaseController == null) {
			ObjectContext objectContext = cayenneService.sharedContext();

			List<Long> orderedClassesIds = Arrays.asList(5021693L,
					5021692L,
					5021691L,
					5021690L);
			List<Long> productIds = cookiesService.getCookieCollectionValue(Product.SHORTLIST_COOKIE_KEY, Long.class);

			SelectQuery selectQuery = new SelectQuery(CourseClass.class);
			selectQuery.setPageSize(3);
			selectQuery.setFetchLimit(3);
			List<CourseClass> courseClasses = objectContext.performQuery(selectQuery);

			List<Product> products = voucherService.loadByIds(productIds);

			PurchaseModel model = new PurchaseModel();
			model.setObjectContext(cayenneService.newContext());
			model.setClasses(model.localizeObjects(courseClasses));
			model.setProducts(model.localizeObjects(products));
			model.setCollege(model.localizeObject(webSiteService.getCurrentCollege()));

			purchaseController = purchaseControllerBuilder.build(model);
			purchaseController.performAction(new ActionParameter(Action.init));
			testAddContact();
			testProceedToPayment();
			testMakePayment();
		}
	}

	private void testMakePayment() {
		PaymentEditorDelegate delegate = purchaseController.getPaymentEditorDelegate();
		delegate.getPaymentIn().setCreditCardCVV("1111");
		delegate.getPaymentIn().setCreditCardExpiry("12/2020");
		delegate.getPaymentIn().setCreditCardName("NAME NAME");
		delegate.getPaymentIn().setCreditCardNumber("9999990000000378");
		delegate.getPaymentIn().setCreditCardType(CreditCardType.VISA);
		delegate.makePayment();
	}

	private void testProceedToPayment() {
		ActionParameter actionParameter = new ActionParameter(Action.proceedToPayment);
		actionParameter.setValue(purchaseController.getModel().getPayment());
		purchaseController.performAction(actionParameter);
	}

	private void testAddContact() {
		ContactCredentials contactCredentials = new ContactCredentials();
		contactCredentials.setLastName("taree3");
		contactCredentials.setFirstName("taree3");
		contactCredentials.setEmail("taree3@taree3.de");
		ActionParameter actionParameter = new ActionParameter(Action.addContact);
		actionParameter.setValue(contactCredentials);
		purchaseController.performAction(actionParameter);
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
		return EnrolCoursesController.HTTP_PROTOCOL + request.getServerName() + "/"+ Courses.class.getSimpleName();
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

	public Object onException(Throwable cause)
	{
		if (purchaseController != null)
		{
			throw new IllegalArgumentException(cause);
		}
		else
		{
			expired = true;
			LOGGER.warn("Persist properties have been cleared. User used two or more tabs or session was expired", cause);
		}
		return this;
	}

}
