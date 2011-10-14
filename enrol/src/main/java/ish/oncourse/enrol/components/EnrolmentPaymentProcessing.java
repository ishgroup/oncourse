package ish.oncourse.enrol.components;

import ish.oncourse.model.Enrolment;
import ish.oncourse.model.EnrolmentStatus;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.services.paymentexpress.IPaymentGatewayService;

import java.util.List;

import org.apache.tapestry5.annotations.InjectComponent;
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
		Session session = request.getSession(true);

		if (!Boolean.TRUE.equals(session.getAttribute(PaymentIn.PAYMENT_PROCESSED_PARAM))) {
			session.setAttribute(PaymentIn.PAYMENT_PROCESSED_PARAM, Boolean.TRUE);
			if (enrolments != null) {
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
				}
				// if the invoice's status is successful, then the whole
				// checkout is
				// successful
				if (!InvoiceStatus.SUCCESS.equals(invoice.getStatus())) {
					session.setAttribute("failedPayment", payment);
				}
			}

			session.setAttribute(PaymentIn.PAYMENT_PROCESSED_PARAM, null);
		} else {
			while (Boolean.TRUE.equals(session.getAttribute(PaymentIn.PAYMENT_PROCESSED_PARAM))) {
				Thread.sleep(1000);
			}
		}
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
