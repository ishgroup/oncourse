package ish.oncourse.services.textile.renderer;

import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.validator.VideoTextileValidator;
import ish.oncourse.util.ValidationErrors;

import java.util.Map;

public class VideoTextileRenderer extends AbstractRenderer {

	public VideoTextileRenderer() {
		validator = new VideoTextileValidator();
	}

	public String render(String tag, ValidationErrors errors) {
		tag = super.render(tag, errors);
		if (!errors.hasFailures()) {
			Map<String, String> tagParams = TextileUtil.getTagParams(tag,
					TextileUtil.PARAM_ID, TextileUtil.PARAM_WIDTH,
					TextileUtil.PARAM_HEIGHT);
			String id = tagParams.get(TextileUtil.PARAM_ID);
			String width = tagParams.get(TextileUtil.PARAM_WIDTH);
			String height = tagParams.get(TextileUtil.PARAM_HEIGHT);
			if (width == null) {
				width = TextileUtil.VIDEO_WIDTH_DEFAULT;
			}
			if (height == null) {
				height = TextileUtil.VIDEO_HEIGHT_DEFAULT;
			}
			String size = " width=\"" + width + "\" height=\"" + height + "\"";
			tag = "<object"
					+ size
					+ "><param name=\"movie\" value=\"http://www.youtube.com/v/"
					+ id
					+ "\">"
					+ "</param><param name=\"allowFullScreen\" value=\"true\"></param>"
					+ "<param name=\"allowscriptaccess\" value=\"always\"></param> <embed type=\"application/x-shockwave-flash\" src=\"http://www.youtube.com/v/"
					+ id
					+ "\" allowscriptaccess=\"always\" allowfullscreen=\"true\" "
					+ size + "></embed></object>";
		}
		return tag;
	}

}
