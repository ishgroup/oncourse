package ish.oncourse.enrol.components;

import java.util.ArrayList;
import java.util.List;

import ish.common.types.PaymentStatus;
import ish.oncourse.enrol.components.AnalyticsTransaction.Item;
import ish.oncourse.enrol.components.AnalyticsTransaction.Transaction;
import ish.oncourse.model.Course;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.Tag;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;

import org.apache.cayenne.PersistenceState;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

public class EnrolmentPaymentResult {

	@Inject
	private Messages messages;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ITagService tagService;

	private PaymentIn payment;

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
	void beforeRender() {
		collegeName = webSiteService.getCurrentCollege().getName();
		thxMsg = messages.format("thanksForEnrolment", collegeName);
		paymentFailedMsg = messages.format("paymentFailed", collegeName);
		enrolmentFailedMsg = messages.format("enrolmentFailed", collegeName);

		initAnalyticTransaction();

	}

	private void initAnalyticTransaction() {
		String googleAnalyticsAccount = webSiteService.getCurrentWebSite()
				.getGoogleAnalyticsAccount();

		if (googleAnalyticsAccount != null && !googleAnalyticsAccount.equals("")) {
			if (payment.getPersistenceState() == PersistenceState.COMMITTED) {
				if (payment != null && isEnrolmentSuccessful() && !enrolments.isEmpty()) {
					List<Item> transactionItems = new ArrayList<Item>(enrolments.size());
					for (Enrolment enrolment : enrolments) {
						Item item = new Item();

						for (Tag tag : tagService.getTagsForEntity(Course.class.getSimpleName(),
								enrolment.getCourseClass().getCourse().getId())) {
							if ("Subjects".equalsIgnoreCase(tag.getRoot().getName())) {
								item.setCategoryName(tag.getDefaultPath().replace('/', '.')
										.substring(1));
								break;
							}
						}
						item.setProductName(enrolment.getCourseClass().getCourse().getName());
						item.setQuantity(1);
						item.setSkuCode(enrolment.getCourseClass().getCourse().getCode());
						item.setUnitPrice(enrolment.getDiscountedExTaxAmount());
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
					transaction.setTax(payment.getTotalGst());
					transaction.setTotal(payment.getTotalAmount());
				}
			}
		}
	}

	public boolean isEnrolmentSuccessful() {
		return "Success".equals(payment.getStatus());
	}

	public boolean isEnrolmentQueued() {
		// Payment.STATUS_QUEUED.equals( getResult() ) ||
		// Payment.STATUS_IN_TRANSACTION.equals( getResult() )
		return false;
	}

	public boolean isEnrolmentFailed() {
		// Payment.STATUSES_FAILED.containsObject( getResult() )
		return "Failed".equals(payment.getStatus());
	}

	public boolean isPayment() {
		// BigDecimal totalAmount;

		// totalAmount = getPaymentQuery().totalAmount();
		// return totalAmount != null && totalAmount.doubleValue() != 0.00d;
		return true;
	}

	public String getPaymentId() {
		return "" + payment.getId();
	}

	public void setPayment(PaymentIn payment) {
		this.payment = payment;

	}

	public void setEnrolments(List<Enrolment> enrolments) {
		this.enrolments = enrolments;
	}
}
