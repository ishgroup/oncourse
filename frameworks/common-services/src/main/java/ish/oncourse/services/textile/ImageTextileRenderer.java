package ish.oncourse.services.textile;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.util.ValidationErrors;

public class ImageTextileRenderer extends AbstractRenderer {

	public ImageTextileRenderer() {
		validator = new ImageTextileValidator();
	}

	public String render(String tag, ValidationErrors errors, Object dataService) {
		IBinaryDataService binaryDataService = (IBinaryDataService) dataService;
		tag = super.render(tag, errors, binaryDataService);
		if (!errors.hasFailures()) {
			BinaryInfo imageBinaryInfo = null;
			if (tag.matches(TextileUtil.IMAGE_ID_REGEXP)) {
				String id = TextileUtil.getValueInFirstQuots(tag);
				imageBinaryInfo = binaryDataService.getBinaryInfo(
						BinaryInfo.ID_PK_COLUMN, Long.valueOf(id));
			} else if (tag.matches(TextileUtil.IMAGE_NAME_REGEXP)) {
				String name = TextileUtil.getValueInFirstQuots(tag);
				imageBinaryInfo = binaryDataService.getBinaryInfo(
						BinaryInfo.NAME_PROPERTY, name);
			}

			// TODO replace this by reading BinaryData content
			// //////////////////////////////
			String extension = imageBinaryInfo.getMimeType().replace("image/",
					"");
			if ("jpeg".equals(extension)) {
				extension = "jpg";
			}
			String path = imageBinaryInfo.getName() + "." + extension;
			// ////////////////////////////

			// the image content will be displayed by ImageServlet
			path = "/servlet/image?id=" + imageBinaryInfo.getId();
			tag = "<img src=\"" + path + "\"/>";
		}
		return tag;
	}

}
