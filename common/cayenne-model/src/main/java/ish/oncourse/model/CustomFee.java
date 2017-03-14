package ish.oncourse.model;

import ish.oncourse.model.auto._CustomFee;
import ish.oncourse.utils.QueueableObjectUtils;

public class CustomFee extends _CustomFee {

    private static final long serialVersionUID = 1L;
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

}
