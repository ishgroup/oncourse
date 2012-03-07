package ish.oncourse.enrol.components;

import ish.common.types.PaymentStatus;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Invoice;
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
		final Session session = request.getSession(false);
        Object paymentProcessedParam = null;
        synchronized (session)
        {
            paymentProcessedParam  =  session.getAttribute(PaymentIn.PAYMENT_PROCESSED_PARAM);
            if (paymentProcessedParam != null)
            {
                paymentProcessedParam = Boolean.TRUE;
                session.setAttribute(PaymentIn.PAYMENT_PROCESSED_PARAM, paymentProcessedParam);
            }
        }
            
		if (!Boolean.TRUE.equals(paymentProcessedParam)) {
			session.setAttribute(PaymentIn.PAYMENT_PROCESSED_PARAM, Boolean.TRUE);
			if (enrolments != null) {
				paymentGatewayService.performGatewayOperation(payment);
				if (!PaymentStatus.SUCCESS.equals(payment.getStatus())) {
					session.setAttribute(PaymentIn.FAILED_PAYMENT_PARAM, payment);
				}
				else {
					//PaymentIn success so commit.
					payment.getObjectContext().commitChanges();
				}
			}
            
            synchronized (session)
            {
                session.setAttribute(PaymentIn.PAYMENT_PROCESSED_PARAM, null);
            }
		} else {
			while (!session.isInvalidated() && Boolean.TRUE.equals(paymentProcessedParam)) {
				Thread.sleep(1000);
                synchronized (session)
                {
                    paymentProcessedParam = session.getAttribute(PaymentIn.PAYMENT_PROCESSED_PARAM);
                }
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
