package ish.oncourse.model;

import java.util.Date;

import ish.oncourse.model.auto._Module;
import ish.oncourse.utils.QueueableObjectUtils;

public class Module extends _Module {
	private static final long serialVersionUID = 806910779727550511L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	/* 
	 * @see ish.oncourse.model.auto._Module#onPreUpdate()
	 */
	@Override
	protected void onPreUpdate() {
		setModified(new Date());
	}

	/* 
	 * @see ish.oncourse.model.auto._Module#onPrePersist()
	 */
	@Override
	protected void onPrePersist() {
		Date today = new Date();
		
		if(getCreated() == null) {
			setCreated(today);
		}
		
		if (getModified() == null) {
			setModified(today);
		}
	}
}
