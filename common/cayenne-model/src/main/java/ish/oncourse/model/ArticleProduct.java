package ish.oncourse.model;

import ish.oncourse.model.auto._ArticleProduct;
import ish.oncourse.utils.QueueableObjectUtils;

public class ArticleProduct extends _ArticleProduct implements Queueable {
	private static final long serialVersionUID = -7624202404417919994L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}

	@Override
	public void setCustomFieldValue(String key, String value) {
		setCustomFieldValue(key, value, ArticleProductCustomField.class);
	}
}
