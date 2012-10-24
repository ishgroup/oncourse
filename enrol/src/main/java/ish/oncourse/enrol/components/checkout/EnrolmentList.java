package ish.oncourse.enrol.components.checkout;

import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.checkout.PurchaseController.Action;
import ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Enrolment;
import org.apache.tapestry5.Block;
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

	@Property
	private Integer index;

	@Inject
	private Request request;

	@Property
	@Parameter(required = false)
	private Block blockToRefresh;


	public Integer getContactIndex()
	{
		return purchaseController.getModel().getContacts().indexOf(contact);
	}

	public Boolean getChecked()
	{
		return  purchaseController.getModel().isEnrolmentEnabled(enrolment);
	}

	public List<Enrolment> getEnrolments() {
		return purchaseController.getModel().getAllEnrolments(contact);
	}

	public EnrolmentItem.EnrolmentItemDelegate getEnrolmentItemDelegate() {
		return new EnrolmentItem.EnrolmentItemDelegate() {
			@Override
			public void onChange(Integer contactIndex, Integer enrolmentIndex) {
				Contact contact = purchaseController.getModel().getContacts().get(contactIndex);
				Enrolment enrolment = purchaseController.getModel().getAllEnrolments(contact).get(enrolmentIndex);
				Boolean isSelected = purchaseController.getModel().isEnrolmentEnabled(enrolment);
				ActionParameter actionParameter = new ActionParameter(isSelected ? Action.DISABLE_ENROLMENT : Action.ENABLE_ENROLMENT);
				actionParameter.setValue(enrolment);
				purchaseController.performAction(actionParameter);
			}
		};
	}
}
