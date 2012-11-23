package ish.oncourse.enrol.checkout.contact;

import ish.oncourse.enrol.checkout.ADelegate;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.model.Contact;
import ish.oncourse.services.preference.ContactFieldHelper;
import ish.oncourse.services.preference.ContactFieldHelper.FieldDescriptor;
import org.apache.cayenne.ObjectContext;

import java.util.List;

public class ContactEditorController extends ADelegate implements ContactEditorDelegate {

	private Contact contact;

	private ObjectContext objectContext;

	private boolean fillRequiredProperties;

	private List<String> visibleFields;

	private ContactFieldHelper contactFieldHelper;

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
		objectContext.commitChangesToParent();
		PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.addContact);
		actionParameter.setErrors(getErrors());
		actionParameter.setValue(contact);
		getPurchaseController().performAction(actionParameter);
	}


	public List<String> getVisibleFields() {
		if (visibleFields == null) {
			visibleFields = contactFieldHelper.getVisibleFields(contact, isFillRequiredProperties());
		}
		if (visibleFields.size() < 1)
			throw new IllegalArgumentException();
		return visibleFields;
	}

	private boolean isVisible(FieldDescriptor descriptor) {
		if (isFillRequiredProperties())
			return getContactFieldHelper().isRequiredField(descriptor) && contact.readProperty(descriptor.propertyName) == null;
		else
			return getContactFieldHelper().isShowField(descriptor);

	}

	public ContactFieldHelper getContactFieldHelper() {
		if (contactFieldHelper == null) {
			contactFieldHelper = new ContactFieldHelper(getPurchaseController().getPreferenceController());
		}
		return contactFieldHelper;
	}

	@Override
	public void cancelContact() {
		//do nothing, just forget about the child objectContext.
		PurchaseController.ActionParameter actionParameter = new PurchaseController.ActionParameter(PurchaseController.Action.cancelAddContact);
		actionParameter.setValue(contact);
		getPurchaseController().performAction(actionParameter);
	}

	public void setObjectContext(ObjectContext objectContext) {
		this.objectContext = objectContext;
	}
}
