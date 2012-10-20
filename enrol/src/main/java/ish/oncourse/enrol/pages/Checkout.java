package ish.oncourse.enrol.pages;

import ish.oncourse.enrol.services.concessions.IConcessionsService;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.enrol.utils.PurchaseController;
import ish.oncourse.enrol.utils.PurchaseController.Action;
import ish.oncourse.enrol.utils.PurchaseController.ActionParameter;
import ish.oncourse.enrol.utils.PurchaseModel;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Product;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.selectutils.ListSelectModel;
import ish.oncourse.selectutils.ListValueEncoder;
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
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Id;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.apache.tapestry5.util.TextStreamResponse;

import java.util.Arrays;
import java.util.List;

public class Checkout {
	public static final Logger LOGGER = Logger.getLogger(Checkout.class);


	public static final String PROPERTY_CONTACT_FULL_NAME = "fullName";
	
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

	/**
	 * The property needs to get value after if payer has been changed
	 */
	@Property
	private Contact selectedPayer;

	public String getAddPayerURL() {
		return "http://" + request.getServerName() + request.getContextPath() + "/" + AddPayer.class.getSimpleName().toLowerCase();
	}
	
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
	
	public boolean isShowConcessionsArea() {
        return concessionsService.hasActiveConcessionTypes();
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

			purchaseController = new PurchaseController();
			purchaseController.setModel(model);
			purchaseController.setDiscountService(discountService);
			purchaseController.setInvoiceProcessingService(invoiceProcessingService);
			purchaseController.setVoucherService(voucherService);
			purchaseController.performAction(new ActionParameter(Action.INIT));
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


			selectQuery = new SelectQuery(StudentConcession.class);
			selectQuery.setPageSize(3);
			selectQuery.setFetchLimit(3);
			List<StudentConcession> concessions = objectContext.performQuery(selectQuery);

			PurchaseModel model = new PurchaseModel();
			model.setObjectContext(cayenneService.newContext());
			model.setClasses(model.localizeObjects(courseClasses));
			model.setProducts(model.localizeObjects(products));
			model.setPayer(model.localizeObject(concessions.get(0).getStudent().getContact()));
			model.addContact(model.getPayer());

			purchaseController = new PurchaseController();
			purchaseController.setModel(model);
			purchaseController.setDiscountService(discountService);
			purchaseController.setInvoiceProcessingService(invoiceProcessingService);
			purchaseController.setVoucherService(voucherService);
			purchaseController.performAction(new ActionParameter(Action.INIT));

			ActionParameter parameter = new ActionParameter(Action.ADD_STUDENT);
			parameter.setValue(concessions.get(1).getStudent().getContact());
			purchaseController.performAction(parameter);

			parameter = new ActionParameter(Action.ADD_STUDENT);
			parameter.setValue(concessions.get(2).getStudent().getContact());
			purchaseController.performAction(parameter);

		}
	}

	public ListSelectModel<Contact> getPayersModel()
	{
		return new ListSelectModel<Contact>(getPurchaseController().getModel().getContacts(), PROPERTY_CONTACT_FULL_NAME, propertyAccess);
	}

	public ListValueEncoder<Contact> getPayersEncoder()
	{
		return new ListValueEncoder<Contact>(getPurchaseController().getModel().getContacts(), Contact.ID_PK_COLUMN, propertyAccess);
	}


	public String getNextPage() {
		return Checkout.class.getSimpleName();
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
    
    /**
     * Create checkSession StreamResponse.
     * @return Text stream response.
     */
    public StreamResponse onActionFromCheckSession() {
    	return checkSession(request.getSession(false));
    }
    
    /**
     * Generate JSON response with result is session time outed or still alive.
     * @param session session for check
     * @return Text stream response.
     */
    public static StreamResponse checkSession(Session session) {
    	JSONObject obj = new JSONObject();
    	if (session == null) {
    		obj.put("status", "session timeout");
    	} else {
    		obj.put("status", "session alive");
    	}
    	return new TextStreamResponse("text/json", obj.toString());
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

}
