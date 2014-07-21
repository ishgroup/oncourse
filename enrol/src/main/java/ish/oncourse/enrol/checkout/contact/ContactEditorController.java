package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.enrol.checkout.*;
import ish.oncourse.model.Contact;
import ish.oncourse.model.CustomField;
import ish.oncourse.services.preference.ContactFieldHelper;
import org.apache.cayenne.ObjectContext;

import java.util.List;

import static ish.oncourse.services.preference.PreferenceController.ContactFiledsSet;

public class ContactEditorController extends ADelegate implements ContactEditorDelegate {

	private Contact contact;

	private ObjectContext objectContext;

	private boolean fillRequiredProperties;

	private List<String> visibleFields;

	private ContactFieldHelper contactFieldHelper;

	private ContactFiledsSet contactFiledsSet;

	private AConcessionDelegate concessionDelegate;

    private PurchaseController.Action addAction;

    private PurchaseController.Action cancelAction;


	@Override
	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	@Override
	public boolean isFillRequiredProperties() {
		return fillRequiredProperties;
	}


	public void setFillRequiredProperties(boolean fillRequiredProperties) {
		this.fillRequiredProperties = fillRequiredProperties;
	}

	@Override
	public void saveContact() {
		PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(addAction);
		actionParameter.setErrors(getErrors());
		actionParameter.setValue(contact);
		getPurchaseController().performAction(actionParameter);
	}


	public List<String> getVisibleFields() {
		if (visibleFields == null) {
			visibleFields = getContactFieldHelper().getVisibleFields(contact, isFillRequiredProperties());
		}
		if (visibleFields.size() < 1)
			throw new IllegalArgumentException();
		return visibleFields;
	}

	public ContactFieldHelper getContactFieldHelper() {
		if (contactFieldHelper == null) {
			contactFieldHelper = new ContactFieldHelper(getPurchaseController().getPreferenceController(),contactFiledsSet);
		}
		return contactFieldHelper;
	}

	@Override
	public void cancelContact() {
		//do nothing, just forget about the child objectContext.
		PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(cancelAction);
		actionParameter.setValue(contact);
		getPurchaseController().performAction(actionParameter);
	}

	public void setObjectContext(ObjectContext objectContext) {
		this.objectContext = objectContext;
	}

	public ContactFiledsSet getContactFiledsSet() {
		return contactFiledsSet;
	}

	public void setContactFiledsSet(ContactFiledsSet contactFiledsSet) {
		this.contactFiledsSet = contactFiledsSet;
	}

	@Override
	public boolean isActiveConcessionTypes() {
		return getPurchaseController().isActiveConcessionTypes();
	}

	@Override
	public ConcessionDelegate getConcessionDelegate() {
		if (concessionDelegate == null)
		{
			concessionDelegate = new AConcessionDelegate() {
				@Override
				public boolean saveConcession() {
					PurchaseController.ActionParameter parameter = new PurchaseController.ActionParameter(PurchaseController.Action.addConcession);
					parameter.setValue(concessionDelegate.getStudentConcession());
					ActionAddConcession action = PurchaseController.Action.addConcession.createAction(getPurchaseController());
					return action.action();
				}
			};
			concessionDelegate.setContact(contact);
			concessionDelegate.setObjectContext(objectContext);

		}
		return concessionDelegate;
	}

    public void setAddAction(PurchaseController.Action addAction) {
        this.addAction = addAction;
    }

    public void setCancelAction(PurchaseController.Action cancelAction) {
        this.cancelAction = cancelAction;
    }

	@Override
	public List<CustomField> getCustomFields() {
		return contact.getCustomFields();
	}
}
