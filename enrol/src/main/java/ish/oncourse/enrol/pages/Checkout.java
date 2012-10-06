package ish.oncourse.enrol.pages;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import ish.oncourse.enrol.services.concessions.IConcessionsService;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.enrol.utils.PurchaseController;
import ish.oncourse.enrol.utils.PurchaseController.Action;
import ish.oncourse.enrol.utils.PurchaseController.ActionParameter;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Product;
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

import org.apache.log4j.Logger;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.apache.tapestry5.util.TextStreamResponse;

public class Checkout {
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
	private PurchaseController controller;
	
	@SuppressWarnings("all")
	@InjectComponent
	private Zone payersZone;
	
	@SuppressWarnings("all")
	@InjectComponent
	@Property
	private Form payerForm;
	
	@Property
    private Contact contact;
	
	@SuppressWarnings("all")
	@Property
	private ListSelectModel<Contact> payersModel;
	
	@SuppressWarnings("all")
	@Property
	private ListValueEncoder<Contact> payersEncoder;
	
	@Inject
	private PropertyAccess propertyAccess;
	
    private Contact currentPayerContact;
    
    @SuppressWarnings("unused")
    @Property
    private int studentIndex;
	
	/**
	 * @return the controller
	 */
	public PurchaseController getController() {
		return controller;
	}
	
	public boolean isNeedInitPayer() {
		return getController() == null;
	}
	
	public String getAddPayerURL() {
		return "http://" + request.getServerName() + request.getContextPath() + "/" + AddPayer.class.getSimpleName().toLowerCase();
	}
	
	private boolean isPayerSelected() {
		return !studentService.getStudentsFromShortList().isEmpty();
	}
	
	public boolean isSomethingToCheckout() {
		return getController() != null && !getController().isErrorEmptyState();
	}
	
	public String getCoursesListLink() {
        return "http://" + request.getServerName() + "/" + Courses.class.getSimpleName().toLowerCase();
    }
	
	/**
	 * Check is current contact is a payer for payment.
	 * @return
	 */
	public boolean isPayer() {
		return getController().getModel().getPayer().getId().equals(contact.getId());
	}
	
	@OnEvent(value = EventConstants.VALUE_CHANGED, component = "currentPayer")
    public Object updatePayer() throws MalformedURLException {
		if (!request.isXHR()) {
			return new URL(request.getServerName());
		}
		ActionParameter actionParameter = new ActionParameter(Action.CHANGE_PAYER);
		actionParameter.setValue(currentPayerContact);//TODO: check me
		getController().performAction(actionParameter);
		return getNextPage();
    }
	
	public Contact getCurrentPayerContact() {
		currentPayerContact = getController().getModel().getPayer();
		return currentPayerContact;
	}
	
	public void setCurrentPayerContact(Contact payer) {
		currentPayerContact = payer;
	}
	
	public boolean isShowConcessionsArea() {
        return concessionsService.hasActiveConcessionTypes();
    }

	@SetupRender
	void beforeRender() {
		synchronized (this) {
			if (!isSomethingToCheckout()) {
				controller = null;
			}
			if (getController() == null) {
				if (isPayerSelected()) {
					List<Long> orderedClassesIds = cookiesService.getCookieCollectionValue(CourseClass.SHORTLIST_COOKIE_KEY, Long.class);
					List<CourseClass> courseClasses = courseClassService.loadByIds(orderedClassesIds);
					List<Long> productIds = cookiesService.getCookieCollectionValue(Product.SHORTLIST_COOKIE_KEY, Long.class);
					List<Product> products = voucherService.loadByIds(productIds);
					List<Contact> selectedContacts = studentService.getStudentsFromShortList();
					if (selectedContacts.size() > 1) {
						LOGGER.debug(String.format(" %s contacts loaded from shortlist for init the controller but should be 1!", selectedContacts.size()));
					}
					controller = new PurchaseController(invoiceProcessingService, discountService, voucherService, cayenneService.newContext(), 
					webSiteService.getCurrentCollege(), selectedContacts.get(0), courseClasses, discountService.getPromotions(), products);
				}
			}
			if (isSomethingToCheckout()) {
				payersModel = new ListSelectModel<Contact>(getController().getModel().getContacts(), "fullName", propertyAccess);
				payersEncoder = new ListValueEncoder<Contact>(getController().getModel().getContacts(), "id", propertyAccess);
			}
		}
	}

	void onPassivate() {
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
     * @param session - session for check.
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
    	if (getController() == null) {
			LOGGER.warn("Persist properties have been cleared. User used two or more tabs", cause);
			return this;
		} else {
			throw new IllegalArgumentException(cause);
		}
    }

}
