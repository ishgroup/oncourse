package ish.oncourse.model;

import ish.oncourse.model.auto._DocumentVersion;
import ish.oncourse.utils.QueueableObjectUtils;

public class DocumentVersion extends _DocumentVersion implements Queueable {
	
	public static final String IMAGE_MIME_TYPE_PREFIX = "image/";

	@Override
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
	
	public boolean isImage() {
		return getMimeType() != null && getMimeType().startsWith(IMAGE_MIME_TYPE_PREFIX);
	}
}
