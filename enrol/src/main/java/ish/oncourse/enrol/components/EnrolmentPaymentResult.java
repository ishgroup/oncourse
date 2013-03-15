package ish.oncourse.enrol.components;

import ish.oncourse.analytics.Item;
import ish.oncourse.analytics.Transaction;
import ish.oncourse.enrol.pages.EnrolCourses;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.enrol.utils.EnrolCoursesController;
import ish.oncourse.enrol.utils.EnrolCoursesModel;
import ish.oncourse.model.*;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.oncourse.ui.pages.Courses;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Session;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class EnrolmentPaymentResult {

	@Inject
	private Messages messages;

	@Inject
	private Request request;

	@SuppressWarnings("all")
	@Inject
	private RequestGlobals requestGlobals;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ITagService tagService;

	@Inject
	private ICookiesService cookiesService;

	@Inject
	private IStudentService studentService;

	@SuppressWarnings("all")
	@Inject
	private IPaymentService paymentService;

	@Inject
	private PreferenceController preferenceController;

	@InjectPage
	private EnrolCourses enrolCourses;

	@SuppressWarnings("all")
	@Property
	private String thxMsg;

	@SuppressWarnings("all")
	@Property
	private String paymentFailedMsg;

	@SuppressWarnings("all")
	@Property
	private String enrolmentFailedMsg;

	private String collegeName;

    private Exception unexpectedException;
    
    @SuppressWarnings("all")
	@InjectComponent
	private Zone successEnrolmentContinueZone;

    @SuppressWarnings("all")
	@InjectComponent
	@Property
	private Form successEnrolmentContinueForm;
    
    @Property
	private Transaction transaction;
    
    /**
	 * @return the controller
	 */
	public EnrolCoursesController getController() {
		return enrolCourses.getController();
	}
	
	public EnrolCoursesModel getModel() {
		return getController().getModel();
	}

	@SetupRender
	Object beforeRender() {
		if (getModel().getInvoice() == null || unexpectedException != null) {
			clearPersistedValues();
			return null;
		}
		if (getController().isEnrolmentSuccessful()) {
			clearPersistedValues();
		}
		collegeName = webSiteService.getCurrentCollege().getName();
		thxMsg = messages.format("thanksForEnrolment", collegeName);
		paymentFailedMsg = messages.format("paymentFailed", collegeName);
		enrolmentFailedMsg = messages.format("enrolmentFailed", collegeName);

		initAnalyticTransaction();
		return null;
	}

	private void clearPersistedValues() {
		// clear all the short lists
		cookiesService.writeCookieValue(CourseClass.SHORTLIST_COOKIE_KEY, StringUtils.EMPTY);
		//cookiesService.writeCookieValue(VoucherProduct.SHORTLIST_COOKIE_KEY, StringUtils.EMPTY);
		cookiesService.writeCookieValue(Discount.PROMOTIONS_KEY, StringUtils.EMPTY);
		enrolCourses.clearPersistedProperties();
		studentService.clearStudentsShortList();
	}

	/**
	 * Fills in the record for google analytics component {@see
	 * AnalyticsTransaction}.
	 */
	private void initAnalyticTransaction() {
		String googleAnalyticsAccount = webSiteService.getCurrentWebSite().getGoogleAnalyticsAccount();

		if (googleAnalyticsAccount != null && StringUtils.trimToNull(googleAnalyticsAccount) != null) {
			if (isPayment() && getController().isEnrolmentSuccessful() && !getModel().getEnrolmentsList().isEmpty()) {
				List<Item> transactionItems = new ArrayList<Item>(getModel().getEnrolmentsList().size());
				for (Enrolment enrolment : getModel().getEnrolmentsList()) {
					Item item = new Item();

					for (Tag tag : tagService.getTagsForEntity(Course.class.getSimpleName(), enrolment.getCourseClass().getCourse().getId())) {
						if (Tag.SUBJECTS_TAG_NAME.equalsIgnoreCase(tag.getRoot().getName())) {
							item.setCategoryName(tag.getDefaultPath().replace('/', '.').substring(1));
							break;
						}
					}
					item.setProductName(enrolment.getCourseClass().getCourse().getName());
					item.setQuantity(1);
					item.setSkuCode(enrolment.getCourseClass().getCourse().getCode());
					item.setUnitPrice(enrolment.getInvoiceLine().getDiscountedPriceTotalExTax().toBigDecimal());
					transactionItems.add(item);
				}
				transaction = new Transaction();
				//getModel().setTransaction(new Transaction());
				transaction.setAffiliation(null);
				transaction.setCity(getModel().getPayment().getContact().getSuburb());
				// TODO only Australia?
				transaction.setCountry("Australia");
				transaction.setItems(transactionItems);
				transaction.setOrderNumber("W" + getModel().getPayment().getId());
				transaction.setShippingAmount(null);
				transaction.setState(getModel().getPayment().getContact().getState());
				BigDecimal tax = new BigDecimal(0);
				for (PaymentInLine pil : getModel().getPayment().getPaymentInLines()) {
					for (InvoiceLine invoiceLine : pil.getInvoice().getInvoiceLines()) {
						tax = tax.add(invoiceLine.getTotalTax().toBigDecimal());
					}
					//tax = tax.add(pil.getInvoice().getTotalGst());
				}
				transaction.setTax(tax);
				transaction.setTotal(getModel().getPayment().getAmount().toBigDecimal());
			}

		}
	}

	/**
	 * Returns true if the payment parameter was passed to the component and
	 * false otherwise.
	 * 
	 * @return
	 */
	private boolean isPayment() {
		return getModel().getPayment() != null;
	}

	public URL onActionFromAbandon() {
        	getController().actionOnAbandon();

            clearPersistedValues();

            Session session = request.getSession(false);
            if (session != null && !session.isInvalidated()) {
                session.setAttribute(PaymentIn.FAILED_PAYMENT_PARAM, null);
            }

            try {
                return new URL(EnrolCoursesController.HTTP_PROTOCOL + request.getServerName());
            } catch (MalformedURLException e) {
            }
            return null;
	}

	public String getPaymentId() {
		if (isPayment()) {
			return getModel().getPayment().getId().toString();
		}
		return getModel().getInvoice().getId().toString();
	}

	public String getCoursesLink() {
		return EnrolCoursesController.HTTP_PROTOCOL + request.getServerName() + "/"+ Courses.class.getSimpleName();
	}

	public String getSuccessUrl() {
		return preferenceController.getEnrolSuccessUrl();
	}

    public Exception getUnexpectedException() {
        return unexpectedException;
    }

    public void setUnexpectedException(Exception unexpectedException) {
        this.unexpectedException = unexpectedException;
    }
        
    @OnEvent(component = "successEnrolmentContinueForm", value = "success")
	Object submitted() {
    	Object resultPage = getSuccessUrl();
    	clearPersistedValues();//It will be good if we clear persisted values before leave the page
    	try {
    		if (resultPage != null) {
    			return new URL(getSuccessUrl());
    		} else {
    			return new URL(getCoursesLink());
    		}
        } catch (MalformedURLException e) {}
    	return null;
    }
    
    public String getContinueButtonText() {
    	return messages.get("continue.button.text");
    }
}
