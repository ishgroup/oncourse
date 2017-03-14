package ish.oncourse.model;

import ish.oncourse.model.auto._Country;
import ish.oncourse.utils.QueueableObjectUtils;

public class Country extends _Country {
	private static final long serialVersionUID = -3212507979616983068L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}
}
