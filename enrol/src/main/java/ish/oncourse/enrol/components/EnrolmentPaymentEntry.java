package ish.oncourse.enrol.components;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class EnrolmentPaymentEntry {
	
	@Parameter
	@Property
	private BigDecimal totalIncGst;
	
	@Property
	private Format moneyFormat;

	@SetupRender
	void beforeRender() {
		moneyFormat = new DecimalFormat("###,##0.00");
	}
	
	public boolean isAllEnrolmentsAvailable(){
		//check if some of the enrolments are corrupted
		return true;
	}
	
	public boolean isZeroPayment(){
		//TODO payment.isZeroTotalIncGst
		return false;
	}
	
	public boolean isHasEnrolmentDisclosure(){
		
		
//<wo:PreferenceConditional college="$myCollege" key="feature.enrolmentDisclosure">
		return true;
	}
	
	public boolean isHasConcessionsCollege(){
		//feature.concessionsInEnrolment
		return true;
	}
	public boolean isHasConcessionsEnrolment(){
		//enrolmentController.hasConcessionPricing
		return true;
	}
	
	public boolean isHasStudentWithoutConcessions(){
		//enrolmentController.hasStudentWithoutConcession
		return true;
	}
	
	public String getSubmitButtonText(){
		//~payment.isZeroTotalIncGst ? 'Confirm enrolment' : 'Make Payment'
		return "Make Payment";
	}
}
