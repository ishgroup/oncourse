package ish.oncourse.model;

import ish.oncourse.model.auto._LicenseFee;
import ish.oncourse.utils.QueueableObjectUtils;

public class LicenseFee extends _LicenseFee {
	private static final long serialVersionUID = 415174116159042943L;
	
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}
}
