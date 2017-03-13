package ish.oncourse.model;

import ish.oncourse.model.auto._TaggableTag;
import ish.oncourse.utils.QueueableObjectUtils;

/**
 * Check the brief description in Taggable class. @see {@link Taggable}
 * 
 * @author anton
 *
 */
public class TaggableTag extends _TaggableTag implements Queueable {
	private static final long serialVersionUID = 1659724430367750104L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return false;
	}
}
