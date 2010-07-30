package ish.oncourse.services.textile;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.College;
import ish.oncourse.util.ValidationErrors;

import org.apache.cayenne.ObjectContext;

public class ImageTextileRenderer extends AbstractRenderer {

	public ImageTextileRenderer() {
		validator = new ImageTextileValidator();
	}

	public String render(String tag, ValidationErrors errors,
			ObjectContext context, College currentCollege) {
		tag = super.render(tag, errors, context, currentCollege);
		if (!errors.hasFailures()) {
			BinaryInfo imageBinaryInfo = null;
			if (tag.matches(TextileUtil.IMAGE_ID_REGEXP)) {
				String id = TextileUtil.getValueInFirstQuots(tag);
				imageBinaryInfo = TextileUtil.getImageBinaryInfo(context,
						BinaryInfo.ID_PK_COLUMN, Long.valueOf(id),
						currentCollege);
			} else if (tag.matches(TextileUtil.IMAGE_NAME_REGEXP)) {
				String name = TextileUtil.getValueInFirstQuots(tag);
				imageBinaryInfo = TextileUtil.getImageBinaryInfo(context,
						BinaryInfo.NAME_PROPERTY, name, currentCollege);
			}

			// TODO replace this by reading BinaryData content
			String extension = imageBinaryInfo.getMimeType().replace("image/",
					"");
			if ("jpeg".equals(extension)) {
				extension = "jpg";
			}
			// TODO get the path for the real image
			String path = imageBinaryInfo.getName() + "." + extension;
			tag = "<img src=\"" + path + "\"/>";
		}
		return tag;
	}

}
