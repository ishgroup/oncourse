package ish.oncourse.model;

import ish.oncourse.common.field.*;
import ish.oncourse.model.auto._Article;
import ish.oncourse.utils.QueueableObjectUtils;

@Type(value = ContextType.ARTICLE)
public class Article extends _Article implements Queueable {
	private static final long serialVersionUID = -7624202404417919994L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Property(value = FieldProperty.CUSTOM_FIELD_ARTICLE, type = PropertyGetSetFactory.SET, params = {String.class, String.class})
	public void setCustomFieldValue(String key, String value) {
		setCustomFieldValue(key, value, ArticleCustomField.class);
	}

	@Property(value = FieldProperty.CUSTOM_FIELD_ARTICLE, type = PropertyGetSetFactory.GET, params = {String.class})
	public String getCustomFieldValue(String key) {
		CustomField field = getCustomField(key);
		return  field == null ? null : field.getValue();
	}

}
