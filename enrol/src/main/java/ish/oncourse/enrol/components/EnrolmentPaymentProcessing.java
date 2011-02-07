package ish.oncourse.enrol.components;

import ish.oncourse.enrol.pages.EnrolCourses;
import ish.oncourse.enrol.services.payment.IPaymentGatewayService;
import ish.oncourse.enrol.services.student.IStudentService;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Discount;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.EnrolmentStatus;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.cookies.ICookiesService;

import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

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
	private Invoice invoice;

	@Persist
	private List<Enrolment> enrolments;

	@InjectPage
	private EnrolCourses enrolCourses;

	@Inject
	private Request request;
	/**
	 * The processHolder displays its content while this method is being
	 * performed and when it is finished, the
	 * {@link EnrolmentPaymentProcessing#result} component is shown instead the
	 * processHolder's content.
	 * 
	 * @return the result block. {@see EnrolmentPaymentResult}
	 */
	@OnEvent(component = "processHolder", value = "progressiveDisplay")
	Object performGateway() {
		if (enrolments != null) {
			// Emulates the gateway delay
			// TODO remove this try/catch block
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (payment != null) {
				paymentGatewayService.performGatewayOperation(payment);
			} else if (enrolments != null) {
				invoice.setStatus(InvoiceStatus.SUCCESS);
				for (Enrolment enrolment : enrolments) {
					enrolment.setStatus(EnrolmentStatus.SUCCESS);
				}
			}

			if (enrolments != null) {
				invoice.getObjectContext().commitChanges();
				enrolCourses.clearPersistedProperties();
			}
			// if the invoice's status is successful, then the whole checkout is
			// successful
			if (InvoiceStatus.SUCCESS.equals(invoice.getStatus())) {
				// clear all the short lists
				cookiesService.writeCookieValue(CourseClass.SHORTLIST_COOKIE_KEY, "");
				cookiesService.writeCookieValue(Discount.PROMOTIONS_KEY, "");
				studentService.clearStudentsShortList();
			}else{
				Session session = request.getSession(false);
				if(session!=null){
					session.setAttribute("failedPayment", payment);
				}
			}

		}
		result.setPayment(payment);
		result.setInvoice(invoice);
		result.setEnrolments(enrolments);
		return result;
	}

	public void setEnrolments(List<Enrolment> enrolments) {
		this.enrolments = enrolments;
	}

	public void setPayment(PaymentIn payment) {
		this.payment = payment;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
}
