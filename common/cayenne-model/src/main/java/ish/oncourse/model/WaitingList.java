package ish.oncourse.model;

import ish.oncourse.common.field.FieldProperty;
import ish.oncourse.common.field.Property;
import ish.oncourse.common.field.PropertyGetSetFactory;
import ish.oncourse.model.auto._WaitingList;
import ish.oncourse.utils.QueueableObjectUtils;

public class WaitingList extends _WaitingList implements Queueable {
	
	private static final long serialVersionUID = 8659761513629004303L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}

	@Override
	@Property(value = FieldProperty.CUSTOM_FIELD_WAITING_LIST, type = PropertyGetSetFactory.SET, params = {String.class, String.class})
	public void setCustomFieldValue(String key, String value) {
		setCustomFieldValue(key, value, WaitingListCustomField.class);
	}
}
