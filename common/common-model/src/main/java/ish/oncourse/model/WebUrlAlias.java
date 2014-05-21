package ish.oncourse.model;

import ish.oncourse.model.auto._WebUrlAlias;
import ish.oncourse.utils.QueueableObjectUtils;

import java.util.Date;

public class WebUrlAlias extends _WebUrlAlias {
	private static final long serialVersionUID = 8310897606553438218L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	protected void onPostAdd() {
		Date today = new Date();
		setCreated(today);
		setModified(today);
        setDefault(false);
	}
}
