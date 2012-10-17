package ish.oncourse.model;

import java.util.Date;

import ish.oncourse.model.auto._Module;
import ish.oncourse.utils.QueueableObjectUtils;

public class Module extends _Module implements Queueable {
	private static final long serialVersionUID = 4023705295199575681L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	/* 
	 * @see ish.oncourse.model.auto._Module#onPreUpdate()
	 */
	@Override
	protected void onPreUpdate() {
		Date today = new Date();
		if (getCreated() == null) {
			setCreated(today);
		}
		setModified(today);
	}

	/* 
	 * @see ish.oncourse.model.auto._Module#onPrePersist()
	 */
	@Override
	protected void onPrePersist() {
		Date today = new Date();		
		if (getCreated() == null) {
			setCreated(today);
		}		
		if (getModified() == null) {
			setModified(today);
		}
	}

	@Override
	public College getCollege() {
		return null;
	}

	@Override
	public void setCollege(College college) {
	}

	@Override
	public void setAngelId(Long angelId) {
	}

	@Override
	public Long getAngelId() {
		return null;
	}
}
