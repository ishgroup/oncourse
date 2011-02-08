package ish.oncourse.model;

import ish.oncourse.model.access.ISHDataContext;
import ish.oncourse.model.auto._Invoice;

public class Invoice extends _Invoice implements Queueable {

	/**
	 * Returns the primary key property - id of {@link Invoice}.
	 * 
	 * @return
	 */
	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

}
