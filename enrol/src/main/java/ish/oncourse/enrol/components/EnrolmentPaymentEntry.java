package ish.oncourse.enrol.components;

import ish.oncourse.enrol.selectutils.ListSelectionModel;
import ish.oncourse.enrol.selectutils.ListValueEncoder;
import ish.oncourse.model.Student;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.List;

import org.apache.tapestry5.Field;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;

public class EnrolmentPaymentEntry {
	
	@Parameter
	@Property
	private BigDecimal totalIncGst;
	
	@Property
	private Format moneyFormat;
	
	@InjectComponent
	private Form paymentDetailsForm;
	
	@Parameter
	@Property
	private List<Student>payers;

	@Property
	@Persist
	private Student payer;
	
	@Property
	@Persist
	private ListSelectionModel<Student> payersModel;
	
	@Property
	@Persist
	private ListValueEncoder<Student> payersEncoder;
	
	private static final String VALID_CLASS = "valid";

	private static final String VALIDATE_CLASS = "validate";

    @Inject
    private PropertyAccess propertyAccess;

	@SetupRender
	void beforeRender() {
		moneyFormat = new DecimalFormat("###,##0.00");
		payer=payers.get(0);
		payersModel = new ListSelectionModel<Student>(payers, "fullName", propertyAccess);
		payersEncoder = new ListValueEncoder<Student>(payers, "id", propertyAccess);
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
	
	public String getCardTypeClass(){
		//TODO getInputSectionClass(cardType)
		return "valid";
	}
	
	private String getInputSectionClass(Field field) {
		ValidationTracker defaultTracker = paymentDetailsForm
				.getDefaultTracker();
		return defaultTracker == null || !defaultTracker.inError(field) ? VALID_CLASS
				: VALIDATE_CLASS;
	}
	
	@OnEvent(component = "paymentDetailsForm", value = "success")
	void submitted() {
		System.out.println(payer.getContact().getFullName());
	}
}
