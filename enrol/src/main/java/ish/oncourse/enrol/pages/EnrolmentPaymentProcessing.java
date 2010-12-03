package ish.oncourse.enrol.pages;

import java.util.List;

import ish.oncourse.enrol.components.EnrolmentPaymentResult;
import ish.oncourse.enrol.services.payment.IPaymentGatewayService;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.PaymentIn;

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
	
	@InjectComponent
	private EnrolmentPaymentResult result;

	@Persist
	private PaymentIn payment;

	@Persist
	private List<Enrolment> enrolments;
	
	public void setPayment(PaymentIn payment){
		this.payment=payment;
	}
	
	@OnEvent(component="processHolder", value="progressiveDisplay")
	Object performGateway(){
		//Emulates the gateway delay
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(paymentGatewayService.performGatewayOperation(payment)){
			payment.getObjectContext().commitChanges();
		}
		result.setPayment(payment);
		result.setEnrolments(enrolments);
		return result;
	}

	public void setEnrolments(List<Enrolment> enrolments) {
		this.enrolments=enrolments;
	}
	
}
