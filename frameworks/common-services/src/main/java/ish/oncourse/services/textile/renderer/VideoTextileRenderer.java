package ish.oncourse.services.textile.renderer;

import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.validator.VideoTextileValidator;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.ValidationErrors;

import java.util.HashMap;
import java.util.Map;

public class VideoTextileRenderer extends AbstractRenderer {

	private IPageRenderer pageRenderer;

	public VideoTextileRenderer(IPageRenderer pageRenderer) {
		this.pageRenderer = pageRenderer;
		validator = new VideoTextileValidator();
	}

	public String render(String tag, ValidationErrors errors) {
		tag = super.render(tag, errors);
		if (!errors.hasFailures()) {
			Map<String, String> tagParams = TextileUtil.getTagParams(tag,
					TextileUtil.PARAM_ID, TextileUtil.PARAM_WIDTH,
					TextileUtil.PARAM_HEIGHT);
			String width = tagParams.get(TextileUtil.PARAM_WIDTH);
			String height = tagParams.get(TextileUtil.PARAM_HEIGHT);
			if (width == null) {
				tagParams.put(TextileUtil.PARAM_WIDTH, TextileUtil.VIDEO_WIDTH_DEFAULT);
			}
			if (height == null) {
				tagParams.put(TextileUtil.PARAM_HEIGHT, TextileUtil.VIDEO_HEIGHT_DEFAULT);
			}
			Map<String, Object> parameters=new HashMap<String, Object>();
			parameters.put("videoParameters", tagParams);
			tag = pageRenderer.renderPage("ui/TextileVideo", parameters);
		}
		return tag;
	}

}
