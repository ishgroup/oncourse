package ish.oncourse.model;

import java.util.Date;

import ish.oncourse.model.auto._Module;

public class Module extends _Module {
	
	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
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
