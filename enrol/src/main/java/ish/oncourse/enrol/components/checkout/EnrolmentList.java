package ish.oncourse.enrol.components.checkout;

import ish.oncourse.enrol.utils.PurchaseController;
import ish.oncourse.enrol.utils.PurchaseController.Action;
import ish.oncourse.enrol.utils.PurchaseController.ActionParameter;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Enrolment;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

public class EnrolmentList {

	@Parameter(required = true)
	private PurchaseController purchaseController;

	@Parameter(required = true)
	@Property
	private Contact contact;

	@Property
	private Enrolment enrolment;

	@Inject
	private Request request;


	public List<Enrolment> getEnrolments()
	{
		return purchaseController.getModel().getAllEnrolments(contact);
	}

    public StreamResponse onActionFromTick(String enrolmentIndex) {
		if (!request.isXHR())
			return null;
		if (!StringUtils.isNumeric(enrolmentIndex))
			return null;

		Integer index = new Integer(enrolmentIndex);
		Enrolment enrolment = purchaseController.getModel().getEnrolmentBy(contact, index);
		Boolean isSelected = purchaseController.getModel().isEnrolmentEnabled(enrolment);
		ActionParameter actionParameter = new ActionParameter(isSelected ? Action.DISABLE_ENROLMENT : Action.ENABLE_ENROLMENT);
		actionParameter.setValue(enrolment);
		purchaseController.performAction(actionParameter);
        return null;
    }
}
