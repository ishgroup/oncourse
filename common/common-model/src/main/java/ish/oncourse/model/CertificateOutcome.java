package ish.oncourse.model;

import ish.oncourse.model.auto._CertificateOutcome;
import ish.oncourse.utils.QueueableObjectUtils;

public class CertificateOutcome extends _CertificateOutcome implements Queueable {
	private static final long serialVersionUID = -7675201567729402744L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}

}
