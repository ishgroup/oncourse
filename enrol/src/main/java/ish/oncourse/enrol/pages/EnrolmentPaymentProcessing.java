package ish.oncourse.enrol.pages;

import java.util.List;

import ish.oncourse.enrol.components.EnrolmentPaymentResult;
import ish.oncourse.enrol.services.payment.IPaymentGatewayService;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.EnrolmentStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentStatus;
import ish.oncourse.services.cookies.ICookiesService;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;

public class EnrolmentPaymentProcessing {

	/**
	 * ish services
	 */
	@Inject
	private IPaymentGatewayService paymentGatewayService;

	@Inject
	private ICookiesService cookiesService;

	@Inject
	private IStudentService studentService;

	@InjectComponent
	private EnrolmentPaymentResult result;

	@Persist
	private PaymentIn payment;

	@Persist
	private List<Enrolment> enrolments;

	public void setPayment(PaymentIn payment) {
		this.payment = payment;
	}

	/**
	 * Method that is performed when the processHolder displays its content and
	 * when it finishes , the result component is shown.
	 * 
	 * @return the result block. {@see EnrolmentPaymentResult}
	 */
	@OnEvent(component = "processHolder", value = "progressiveDisplay")
	Object performGateway() {
		// Emulates the gateway delay
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ObjectContext context = null;
		if (payment != null) {
			paymentGatewayService.performGatewayOperation(payment);
			context = payment.getObjectContext();
		} else if (!enrolments.isEmpty()) {
			// FIXME or some other processing of enrolment without payment
			for (Enrolment enrolment : enrolments) {
				enrolment.setStatus(EnrolmentStatus.SUCCESS);
			}
			context = enrolments.get(0).getObjectContext();
		}

		if (context != null) {
			context.commitChanges();
		}
		// FIXME consider how to deal with "null" payment
		if (payment == null || PaymentStatus.SUCCESS.equals(payment.getStatus())) {
			// clear all the short lists
			cookiesService.writeCookieValue(CourseClass.SHORTLIST_COOKEY_KEY, "");
			cookiesService.writeCookieValue(Discount.PROMOTIONS_KEY, "");
			studentService.clearStudentsShortList();
		}
		result.setPayment(payment);
		result.setEnrolments(enrolments);
		return result;
	}

	public void setEnrolments(List<Enrolment> enrolments) {
		this.enrolments = enrolments;
	}

}
