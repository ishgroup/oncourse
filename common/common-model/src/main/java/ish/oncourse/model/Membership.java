package ish.oncourse.model;

import ish.oncourse.model.auto._Membership;
import ish.oncourse.utils.QueueableObjectUtils;

public class Membership extends _Membership implements Queueable {
	private static final long serialVersionUID = -7624202404417919994L;
	
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

}
