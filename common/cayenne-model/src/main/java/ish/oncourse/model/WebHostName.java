package ish.oncourse.model;

import ish.oncourse.model.auto._WebHostName;
import ish.oncourse.utils.QueueableObjectUtils;

public class WebHostName extends _WebHostName {
	private static final long serialVersionUID = -1619903647679895866L;
	
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}
}
