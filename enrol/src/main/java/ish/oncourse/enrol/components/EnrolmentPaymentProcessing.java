package ish.oncourse.enrol.components;

import ish.oncourse.enrol.pages.EnrolCourses;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.PaymentIn;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static ish.oncourse.enrol.pages.EnrolCourses.HTTP_PROTOCOL;

public class EnrolmentPaymentProcessing {

	/**
	 * ish services
	 */

	@InjectComponent
	private EnrolmentPaymentResult result;

	@Persist
	private PaymentIn payment;

	@Persist
	private Invoice invoice;

	@Persist
	private List<Enrolment> enrolments;

	@Inject
	private Request request;

    @InjectPage
    private EnrolCourses enrolCourses;

	/**
	 * The processHolder displays its content while this method is being
	 * performed and when it is finished, the
	 * {@link EnrolmentPaymentProcessing#result} component is shown instead the
	 * processHolder's content.
	 * 
	 * @return the result block. {@see EnrolmentPaymentResult}
	 * @throws Exception
	 */
	@OnEvent(component = "processHolder", value = "progressiveDisplay")
	Object performGateway() throws Exception {

        /**
         *  Workaround to exclude NullPointerException on context synchronize block. Unknown reason. (possible reason is expired session).
         */
        if (enrolCourses.getContext() == null)
        {
            try {
                return new URL(HTTP_PROTOCOL + request.getServerName());
            } catch (MalformedURLException e) {
            }
        }
        enrolCourses.processPayment();
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

	public PaymentIn getPayment() {
		return payment;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public List<Enrolment> getEnrolments() {
		return enrolments;
	}

}
