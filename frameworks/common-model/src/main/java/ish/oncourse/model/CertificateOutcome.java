package ish.oncourse.model;

import ish.oncourse.model.auto._CertificateOutcome;

public class CertificateOutcome extends _CertificateOutcome implements Queueable {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}
	
}
