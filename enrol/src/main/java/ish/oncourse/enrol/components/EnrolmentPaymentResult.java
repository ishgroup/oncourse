package ish.oncourse.enrol.components;

import ish.common.types.PaymentStatus;
import ish.oncourse.enrol.components.AnalyticsTransaction.Item;
import ish.oncourse.enrol.components.AnalyticsTransaction.Transaction;
import ish.oncourse.enrol.pages.EnrolCourses;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.Tag;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.payment.IPaymentService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;
import ish.persistence.CommonPreferenceController;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;

public class EnrolmentPaymentResult {

	@Inject
	private Messages messages;

	@Inject
	private Request request;

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

	@Inject
	private IPaymentService paymentService;
	
	@Inject
	private CommonPreferenceController preferenceController;

	@InjectPage
	private EnrolCourses enrolCourses;

	@Parameter
	private PaymentIn payment;

	@Parameter
	@Property
	private Invoice invoice;

	@Parameter
	private List<Enrolment> enrolments;

	@Property
	private String thxMsg;

	@Property
	private String paymentFailedMsg;

	@Property
	private String enrolmentFailedMsg;

	private String collegeName;

	@Property
	private Transaction transaction;

	@SetupRender
	Object beforeRender() {
		if (invoice == null) {
			clearPersistedValues();
			return null;
		}
		if (isEnrolmentSuccessful()) {
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
		cookiesService.writeCookieValue(CourseClass.SHORTLIST_COOKIE_KEY, "");
		cookiesService.writeCookieValue(Discount.PROMOTIONS_KEY, "");
		enrolCourses.clearPersistedProperties();
		studentService.clearStudentsShortList();
	}

	/**
	 * Fills in the record for google analytics component {@see
	 * AnalyticsTransaction}.
	 */
	private void initAnalyticTransaction() {
		String googleAnalyticsAccount = webSiteService.getCurrentWebSite().getGoogleAnalyticsAccount();

		if (googleAnalyticsAccount != null && !googleAnalyticsAccount.equals("")) {
			if (payment != null && isEnrolmentSuccessful() && !enrolments.isEmpty()) {
				List<Item> transactionItems = new ArrayList<Item>(enrolments.size());
				for (Enrolment enrolment : enrolments) {
					Item item = new Item();

					for (Tag tag : tagService.getTagsForEntity(Course.class.getSimpleName(), enrolment.getCourseClass()
							.getCourse().getId())) {
						if ("Subjects".equalsIgnoreCase(tag.getRoot().getName())) {
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
				transaction.setAffiliation(null);
				transaction.setCity(payment.getContact().getSuburb());
				// TODO only Australia?
				transaction.setCountry("Australia");
				transaction.setItems(transactionItems);
				transaction.setOrderNumber("W" + payment.getId());
				transaction.setShippingAmount(null);
				transaction.setState(payment.getContact().getState());
				BigDecimal tax = new BigDecimal(0);
				for (PaymentInLine pil : payment.getPaymentInLines()) {
					tax = tax.add(pil.getInvoice().getTotalGst());
				}
				transaction.setTax(tax);
				transaction.setTotal(payment.getAmount());
			}

		}
	}

	/**
	 * Returns true if the enrol operation was successful and false otherwise.
	 * 
	 * @return
	 */
	public boolean isEnrolmentSuccessful() {
		if (!isPayment()) {
			return InvoiceStatus.SUCCESS.equals(invoice.getStatus());
		}
		return PaymentStatus.SUCCESS.equals(payment.getStatus());
	}

	/**
	 * Returns true if the enrol operation was failed and false otherwise.
	 * 
	 * @return
	 */
	public boolean isEnrolmentFailed() {

		if (!isPayment()) {
			return !InvoiceStatus.SUCCESS.equals(invoice.getStatus());
		}
		PaymentStatus status = payment.getStatus();
		return PaymentStatus.FAILED.equals(status) || PaymentStatus.STATUS_REFUNDED.equals(status)
				|| PaymentStatus.FAILED_CARD_DECLINED.equals(status);
	}

	/**
	 * Returns true if the payment parameter was passed to the component and
	 * false otherwise.
	 * 
	 * @return
	 */
	public boolean isPayment() {
		return payment != null;
	}

	public URL onActionFromAbandon() {
		payment.abandonPayment();
		payment.getObjectContext().commitChanges();

		clearPersistedValues();
		try {
			return new URL("http://" + request.getServerName());
		} catch (MalformedURLException e) {
		}
		return null;
	}

	public String getPaymentId() {
		if (isPayment()) {
			return payment.getId().toString();
		}
		return invoice.getId().toString();
	}

	public String getCoursesLink() {
		return "http://" + request.getServerName() + "/courses";
	}
	
	public String getSuccessUrl(){
		return preferenceController.getEnrolSuccessUrl();
	}
}
