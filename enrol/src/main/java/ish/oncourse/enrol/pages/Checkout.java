package ish.oncourse.enrol.pages;

import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.checkout.PurchaseController.Action;
import ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;
import ish.oncourse.enrol.checkout.PurchaseModel;
import ish.oncourse.enrol.checkout.contact.ContactCredentials;
import ish.oncourse.enrol.services.concessions.IConcessionsService;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Product;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.ui.pages.Courses;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Id;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.Request;

import java.util.Arrays;
import java.util.List;

public class Checkout {
	public static final String DATE_FIELD_FORMAT = "MM/dd/yyyy";

	public static final Logger LOGGER = Logger.getLogger(Checkout.class);


	@Inject
    private IInvoiceProcessingService invoiceProcessingService;
	
	@Inject
    private IDiscountService discountService;
	
	@Inject
	private IVoucherService voucherService;
	
	@Inject
    private ICayenneService cayenneService;
	
	@Inject
    private IWebSiteService webSiteService;
	
	@Inject
    private IStudentService studentService;
	
	@Inject
    private ICookiesService cookiesService;
	
	@Inject
    private ICourseClassService courseClassService;
	
	@Inject
	private PreferenceController preferenceController;
	
	@Inject
	private IConcessionsService concessionsService;
	
	@Inject
    private Request request;
		
	@Persist
	private PurchaseController purchaseController;
	
	@Property
    private Contact contact;
	
	@Inject
	private PropertyAccess propertyAccess;

	@Inject
	@Id("checkout")
	private Block checkoutBlock;

	@Inject
	@Id("concession")
	@Property
	private Block blockConcession;

	@Property
    private int studentIndex;

	@Property
	private String error;

	/**
	 * The property needs to get value after if payer has been changed
	 */
	@Property
	private Contact selectedPayer;

	private boolean isPayerSelected() {
		return !studentService.getStudentsFromShortList().isEmpty();
	}
	

	public String getCoursesListLink() {
        return "http://" + request.getServerName() + "/" + Courses.class.getSimpleName().toLowerCase();
    }
	
	/**
	 * Check is current contact is a payer for payment.
	 * @return
	 */
	public boolean isPayer() {
		return getPurchaseController().getModel().getPayer().getId().equals(contact.getId());
	}

	@SetupRender
	void beforeRender() {
		synchronized (this) {
			initTestPaymentController();
		}
	}

	public PurchaseController getPurchaseController()
	{
		return purchaseController;
	}

	private void initPaymentController() {
		if (purchaseController == null) {
			List<Long> orderedClassesIds = cookiesService.getCookieCollectionValue(CourseClass.SHORTLIST_COOKIE_KEY, Long.class);
			List<Long> productIds = cookiesService.getCookieCollectionValue(Product.SHORTLIST_COOKIE_KEY, Long.class);
			List<CourseClass> courseClasses = courseClassService.loadByIds(orderedClassesIds);
			List<Product> products = voucherService.loadByIds(productIds);
			List<Contact> selectedContacts = studentService.getStudentsFromShortList();
			Contact payer = selectedContacts.size() > 0 ? selectedContacts.get(0): null;
			if (selectedContacts.size() > 1) {
				LOGGER.warn(String.format(" %s contacts loaded from shortlist for init the purchaseController but should be 1!", selectedContacts.size()));
			}

			PurchaseModel model = new PurchaseModel();
			model.setObjectContext(cayenneService.newContext());
			model.setClasses(model.localizeObjects(courseClasses));
			model.setProducts(model.localizeObjects(products));
			model.addContact(model.localizeObject(payer));
			model.setPayer(model.localizeObject(payer));
			model.setCollege(model.localizeObject(webSiteService.getCurrentCollege()));

			purchaseController = createPurchaseConroller(model);
			purchaseController.performAction(new ActionParameter(Action.INIT));
		}
	}

	private PurchaseController createPurchaseConroller(PurchaseModel model) {
		PurchaseController purchaseController = new PurchaseController();
		purchaseController.setModel(model);
		purchaseController.setDiscountService(discountService);
		purchaseController.setInvoiceProcessingService(invoiceProcessingService);
		purchaseController.setVoucherService(voucherService);
		purchaseController.setConcessionsService(concessionsService);
		purchaseController.setStudentService(studentService);
		purchaseController.setPreferenceController(preferenceController);
		return purchaseController;
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


			selectQuery = new SelectQuery(StudentConcession.class);
			selectQuery.setPageSize(3);
			selectQuery.setFetchLimit(3);
			List<StudentConcession> concessions = objectContext.performQuery(selectQuery);

			PurchaseModel model = new PurchaseModel();
			model.setObjectContext(cayenneService.newContext());
			model.setClasses(model.localizeObjects(courseClasses));
			model.setProducts(model.localizeObjects(products));
			model.setPayer(model.localizeObject(concessions.get(0).getStudent().getContact()));
			model.setCollege(model.localizeObject(webSiteService.getCurrentCollege()));
			model.addContact(model.getPayer());

			purchaseController = createPurchaseConroller(model);
			purchaseController.performAction(new ActionParameter(Action.INIT));

			for (int i=1; i < concessions.size(); i++)
			{
				Contact contact = concessions.get(i).getStudent().getContact();
				purchaseController.performAction(new ActionParameter(Action.START_ADD_CONTACT));
				ActionParameter parameter = new ActionParameter(Action.ADD_CONTACT);
				ContactCredentials contactCredentials = new ContactCredentials();
				contactCredentials.setEmail(contact.getEmailAddress());
				contactCredentials.setFirstName(contact.getGivenName());
				contactCredentials.setFirstName(contact.getFamilyName());
				parameter.setValue(contactCredentials);
				parameter.setValue(purchaseController.getModel().localizeObject(contact));
				purchaseController.performAction(parameter);
			}
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




    public Object handleUnexpectedException(final Throwable cause) {
    	if (getPurchaseController() == null) {
			LOGGER.warn("Persist properties have been cleared. User used two or more tabs", cause);
			return this;
		} else {
			throw new IllegalArgumentException(cause);
		}
    }

	public Block getCheckoutBlock()
	{
		return checkoutBlock;
	}


	public Object onActionFromAddContactLink()
	{
		if (!request.isXHR())
			return null;
		purchaseController.performAction(new ActionParameter(Action.START_ADD_CONTACT));
		return checkoutBlock;
	}

}
