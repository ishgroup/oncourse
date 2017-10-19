package ish.oncourse.model;

import ish.common.types.ConfirmationStatus;
import ish.oncourse.cayenne.IExpandable;
import ish.oncourse.common.field.FieldProperty;
import ish.oncourse.common.field.Property;
import ish.oncourse.common.field.PropertyGetSetFactory;
import ish.oncourse.model.auto._Application;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

public class Application extends _Application implements Queueable, IExpandable {

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
		CustomField field = getCustomField(key);
		if (field != null) {
			field.setValue(value);
		} else {
			ObjectContext context = getObjectContext();
			CustomFieldType customFieldType = ObjectSelect.query(CustomFieldType.class)
					.where(CustomFieldType.COLLEGE.eq(getCollege()))
					.and(CustomFieldType.KEY.eq(key))
					.and(CustomFieldType.ENTITY_NAME.eq(Application.class.getSimpleName()))
					.selectFirst(context);

			if (customFieldType == null) {
				return;
			}
			ApplicationCustomField customField = context.newObject(ApplicationCustomField.class);
			customField.setValue(value);
			customField.setRelatedObject(this);
			customField.setCustomFieldType(customFieldType);
			customField.setCollege(getCollege());
		}
	}

	private CustomField getCustomField(String key) {
		for (CustomField customField : getCustomFields()) {
			String customFieldKey = customField.getCustomFieldType().getKey();
			if (customFieldKey != null && customFieldKey .equalsIgnoreCase(key)) {
				return customField;
			}
		}
		return null;
	}
}
