package ish.oncourse.model;

import ish.oncourse.model.auto._TaggableTag;
import ish.oncourse.utils.QueueableObjectUtils;

public class TaggableTag extends _TaggableTag implements Queueable {
	private static final long serialVersionUID = 1659724430367750104L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}
	
}
