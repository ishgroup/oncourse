package ish.oncourse.model;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import ish.oncourse.model.auto._BinaryInfo;
import ish.oncourse.utils.QueueableObjectUtils;

public class BinaryInfo extends _BinaryInfo implements Queueable {
	private static final long serialVersionUID = 7381721704078503112L;
	public static final String DISPLAYED_IMAGES_IDS = "displayedImagesIds";

	public Long getId() {
		return QueueableObjectUtils.getId(this);
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
