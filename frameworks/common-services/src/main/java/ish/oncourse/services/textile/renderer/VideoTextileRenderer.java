package ish.oncourse.services.textile.renderer;

import java.util.Map;

import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.validator.VideoTextileValidator;
import ish.oncourse.util.ValidationErrors;

public class VideoTextileRenderer extends AbstractRenderer {

	public VideoTextileRenderer() {
		validator = new VideoTextileValidator();
	}

	public String render(String tag, ValidationErrors errors, Object dataService) {
		tag = super.render(tag, errors, dataService);
		if (!errors.hasFailures()) {
			Map<String, String> tagParams = TextileUtil.getTagParams(tag, "id",
					"width", "height");
			Long id = Long.valueOf(tagParams.get("id"));
			String width = tagParams.get("width");
			String height = tagParams.get("height");
			if (width == null) {
				width = TextileUtil.VIDEO_WIDTH_DEFAULT;
			}
			if (height == null) {
				height = TextileUtil.VIDEO_HEIGHT_DEFAULT;
			}
			String size = " width=\"" + width + "\" height=\"" + height + "\"";
			tag = "<object" + size + "><embed src=\"/servlet/binarydata?id="
					+ id + "\"" + size + "></embed></object>";
		}
		return tag;
	}

}
