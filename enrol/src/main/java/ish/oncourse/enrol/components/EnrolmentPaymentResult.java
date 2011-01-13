package ish.oncourse.enrol.components;

import ish.oncourse.enrol.components.AnalyticsTransaction.Item;
import ish.oncourse.enrol.components.AnalyticsTransaction.Transaction;
import ish.oncourse.model.Course;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.PaymentStatus;
import ish.oncourse.model.Tag;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.tag.ITagService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

	/**
	 * Fills in the record for google analytics component {@see
	 * AnalyticsTransaction}.
	 */
	private void initAnalyticTransaction() {
		String googleAnalyticsAccount = webSiteService.getCurrentWebSite()
				.getGoogleAnalyticsAccount();

		if (googleAnalyticsAccount != null && !googleAnalyticsAccount.equals("")) {
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
					item.setUnitPrice(enrolment.getInvoiceLine().getDiscountedPriceTotalExTax()
							.toBigDecimal());
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
	 * Returns true if the enrol operation was successful and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isEnrolmentSuccessful() {
		if (!isPayment()) {
			return enrolments != null;
		}
		return PaymentStatus.SUCCESS.equals(payment.getStatus());
	}

	/**
	 * Returns true if the enrol operation was failed and false otherwise.
	 * 
	 * @return
	 */
	public boolean isEnrolmentFailed() {
		if (payment == null && enrolments == null) {
			return true;
		}
		if (!isPayment()) {
			return false;
		}
		PaymentStatus status = payment.getStatus();
		return PaymentStatus.FAILED.equals(status) || PaymentStatus.REFUNDED.equals(status);
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
