package ish.oncourse.model;

import ish.oncourse.model.auto._Document;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;

import java.util.Arrays;
import java.util.List;

public class Document extends _Document implements Queueable {

	@Override
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}

	public DocumentVersion getCurrentVersion() {
		List<DocumentVersion> versions = getVersions();

		Ordering.orderList(versions, Arrays.asList(new Ordering(DocumentVersion.TIMESTAMP_PROPERTY, SortOrder.DESCENDING)));

		return versions.get(0);
	}

	@Deprecated
	public String getType() {
		// Logic is copied from {@link BinaryInfo#getType} and is only here 
		// for the purpose of compatibility with angel versions before 5.1
		
		DocumentVersion currentVersion = getCurrentVersion();
		
		if (currentVersion.getMimeType() == null) {
			return "";
		}
		return AttachmentType.getExtentionByMimeType(currentVersion.getMimeType());
	}

	@Deprecated
	public Long getSize() {
		return getCurrentVersion().getByteSize() / 1000;
	}

	@Deprecated
	public Integer getPixelWidth() {
		return getCurrentVersion().getPixelWidth();
	}

	@Deprecated
	public Integer getPixelHeight() {
		return getCurrentVersion().getPixelHeight();
	}
}
