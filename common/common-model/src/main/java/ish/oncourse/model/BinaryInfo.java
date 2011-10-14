package ish.oncourse.model;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import ish.oncourse.model.auto._BinaryInfo;

public class BinaryInfo extends _BinaryInfo implements Queueable {

	public static final String DISPLAYED_IMAGES_IDS = "displayedImagesIds";

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId().getIdSnapshot().get(
				ID_PK_COLUMN) : null;
	}

	public Long getSize() {
		return getByteSize() / 1000;
	}

	public String getType() {
		if (getMimeType() == null) {
			return "";
		}
		return AttachmentType.getExtentionByMimeType(getMimeType());
	}

	public static Expression getImageQualifier() {
		return ExpressionFactory.likeExp(BinaryInfo.MIME_TYPE_PROPERTY, "image/%");
	}

}
