package ish.oncourse.enrol.pages;

import ish.oncourse.enrol.components.EnrolmentPaymentEntry;
import ish.oncourse.enrol.components.EnrolmentPaymentProcessing;
import ish.oncourse.enrol.services.concessions.IConcessionsService;
import ish.oncourse.enrol.services.invoice.IInvoiceProcessingService;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.enrol.utils.EnrolCoursesController;
import ish.oncourse.enrol.utils.EnrolCoursesModel;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CourseClass;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.discount.IDiscountService;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;

import java.util.List;

@Deprecated
public class EnrolCourses {
    public static final Logger LOGGER = Logger.getLogger(EnrolCourses.class);
    public static final String HTTP_PROTOCOL = "http://";

    /**
     * ish services
     */
    @Inject
    private ICookiesService cookiesService;

    @Inject
    private ICourseClassService courseClassService;

    @Inject
    private IConcessionsService concessionsService;

    @Inject
    private IWebSiteService webSiteService;

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private IStudentService studentService;

    @Inject
    private IInvoiceProcessingService invoiceProcessingService;

    @Inject
    private IDiscountService discountService;

    @Inject
    private IPaymentGatewayService paymentGatewayService;

    /**
     * tapestry services
     */
    @Inject
    private Request request;

    @SuppressWarnings("all")
	@Inject
    private RequestGlobals requestGlobals;

    @Inject
    private ComponentResources componentResources;

    @Inject
    private PreferenceController preferenceController;

    @Property
    private Contact contact;

    @SuppressWarnings("unused")
    @Property
    private int studentIndex;

    @SuppressWarnings("unused")
    @InjectComponent
    @Property
    private Zone totals;

    @SuppressWarnings("unused")
    @InjectComponent
    @Property
    private EnrolmentPaymentEntry paymentEntry;

    @InjectComponent
    @Property
    private EnrolmentPaymentProcessing resultComponent;

    //@Property
    @Persist
    private EnrolCoursesController controller;
    
    /**
	 * @return the controller
	 */
	public synchronized EnrolCoursesController getController() {
		return controller;
	}
	
	public synchronized EnrolCoursesModel getModel() {
		return getController().getModel();
	}

	/**
     * Initial setup of the EnrolCourses page. Retrieves all the shortlisted
     * classes and students.<br/>
     * If there're no shortlisted classes, the message for user to return to the
     * courses list and order some is appeared. <br/>
     * If there're no shortlisted students, the user adds it in the proposed
     * form.<br/>
     * When there exist both students and classes list, the payment-related
     * entities are created: {@see EnrolCourses#initPayment()}.
     */
    @SetupRender
    void beforeRender() {
    	synchronized (this) {
			if (getController() == null) {
				controller = new EnrolCoursesController(invoiceProcessingService, paymentGatewayService);
				if (getController().getContext() == null) {
					getController().setContext(cayenneService.newContext());
				}
			}
		}
        // No need to create the entities if the page is used just to display
        // the checkout result, or if the payment processing is disabled at all
        synchronized (getController().getContext()) {
            if (!getController().isCheckoutResult() && isPaymentGatewayEnabled()) {
                initClassesToEnrol();
                if (getModel().getClassesToEnrol() != null) {
                	getModel().setContacts(studentService.getStudentsFromShortList());
                    if (!getModel().getContacts().isEmpty()) {
                        // init the payment and the related entities only if there exist shortlisted classes and students
                    	getController().initPayment(request.getSession(false), webSiteService.getCurrentCollege(), discountService.getPromotions());
                    }
                } else {
                    // if there no shortlisted classes, send user to select them
                    clearPersistedProperties();
                }
            }
        }
    }

    /**
     * Initializes the {@link #classesToEnrol} list. Checks if the
     * {@link #classesToEnrol} needs to be filled(refilled) or should be null:
     * <ul>
     * <li>if there no any shortlisted classes, the {@link #classesToEnrol} is
     * set with null.</li>
     * <li>if the {@link #classesToEnrol} is already filled, then it checks if
     * the shortlisted classes hasn't been changed, and if they hasn't, refill
     * the {@link #classesToEnrol} with the new values.</li>
     * </ul>
     */
    public void initClassesToEnrol() {
        List<Long> orderedClassesIds = cookiesService.getCookieCollectionValue(CourseClass.SHORTLIST_COOKIE_KEY, Long.class);
        getController().deleteNotUsedEnrolments(getController().getEnrolmentsList(), orderedClassesIds);

        if (orderedClassesIds == null || orderedClassesIds.size() < 1) {
        	getModel().setClassesToEnrol(null);
            return;
        }

        if (getController().shouldReloadClassesToEnrol(orderedClassesIds)) {
        	getModel().setClassesToEnrol(courseClassService.loadByIds(orderedClassesIds));
        	getController().orderClassesToEnrol();
        }
    }

    @CleanupRender
    void cleanupRender() {
    	getController().setCheckoutResult(false);
    }

    /**
     * Clears all the properties with the @Persist annotation.
     */
    public void clearPersistedProperties() {
        componentResources.discardPersistentFieldChanges();
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

    public boolean isShowConcessionsArea() {
        return concessionsService.hasActiveConcessionTypes();
    }

    public String getCoursesListLink() {
        return "http://" + request.getServerName() + "/courses";
    }

    /**
     * Returns the embedded {@link EnrolmentPaymentProcessing} component for
     * displaying the checkout results.
     *
     * @return
     */
    public EnrolmentPaymentProcessing getResultingElement() {
        return resultComponent;
    }

    public synchronized StreamResponse onActionFromCheckSession() {
    	return getController().createCheckSessionResponse(request.getSession(false));
    }

    public Object handleUnexpectedException(final Throwable cause) {
    	return getController().handleUnexpectedException(cause);
    }

}
