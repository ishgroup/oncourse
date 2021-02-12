package ish.oncourse.model;

import ish.common.types.ConfirmationStatus;
import ish.oncourse.common.field.*;
import ish.oncourse.model.auto._Application;
import ish.oncourse.utils.QueueableObjectUtils;

@Type(value = ContextType.APPLICATION)
public class Application extends _Application implements Queueable {

	@Override
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}

	@Override
	protected void onPostAdd() {
		if (getConfirmationStatus() == null) {
			setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND);
		}
	}

	@Property(value = FieldProperty.CUSTOM_FIELD_APPLICATION, type = PropertyGetSetFactory.SET, params = {String.class, String.class})
	public void setCustomFieldValue(String key, String value){
		setCustomFieldValue(key, value, ApplicationCustomField.class);
	}

	@Property(value = FieldProperty.CUSTOM_FIELD_APPLICATION, type = PropertyGetSetFactory.GET, params = {String.class})
	public String getCustomFieldValue(String key) {
		CustomField field = getCustomField(key);
		return  field == null ? null : field.getValue();
	}
}
