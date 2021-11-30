package ish.oncourse.model;

import ish.oncourse.model.auto._MembershipProduct;
import ish.oncourse.utils.QueueableObjectUtils;

public class MembershipProduct extends _MembershipProduct implements Queueable {
	private static final long serialVersionUID = 2714540942611732667L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public void setCustomFieldValue(String key, String value) {
		setCustomFieldValue(key, value, MembershipProductCustomField.class);
	}
}
