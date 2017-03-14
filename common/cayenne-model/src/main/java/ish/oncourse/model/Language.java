package ish.oncourse.model;

import ish.oncourse.model.auto._Language;
import ish.oncourse.utils.QueueableObjectUtils;

public class Language extends _Language {
	private static final long serialVersionUID = 2587484920318036801L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}
}
