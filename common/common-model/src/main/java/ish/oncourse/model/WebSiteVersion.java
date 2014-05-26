package ish.oncourse.model;

import ish.oncourse.model.auto._WebSiteVersion;

public class WebSiteVersion extends _WebSiteVersion {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? ((Number) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN)).longValue() : null;
	}
}
