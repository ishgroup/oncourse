package ish.oncourse.services.textile.renderer;

import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.VideoTextileAttributes;
import ish.oncourse.services.textile.validator.VideoTextileValidator;
import ish.oncourse.util.IPageRenderer;

import java.util.HashMap;
import java.util.Map;

public class VideoTextileRenderer extends AbstractRenderer {

	private IPageRenderer pageRenderer;

	public VideoTextileRenderer(IPageRenderer pageRenderer) {
		this.pageRenderer = pageRenderer;
		validator = new VideoTextileValidator();
	}

	@Override
	protected String internalRender(String tag) {
		Map<String, String> tagParams = TextileUtil.getTagParams(tag,
				VideoTextileAttributes.getAttrValues());
		String width = tagParams
				.get(VideoTextileAttributes.VIDEO_PARAM_WIDTH.getValue());
		String height = tagParams
				.get(VideoTextileAttributes.VIDEO_PARAM_HEIGHT.getValue());
		if (width == null) {
			tagParams.put(VideoTextileAttributes.VIDEO_PARAM_WIDTH
					.getValue(), TextileUtil.VIDEO_WIDTH_DEFAULT);
		}
		if (height == null) {
			tagParams.put(VideoTextileAttributes.VIDEO_PARAM_HEIGHT
					.getValue(), TextileUtil.VIDEO_HEIGHT_DEFAULT);
		}
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(TextileUtil.TEXTILE_VIDEO_PAGE_PARAM, tagParams);
		tag = pageRenderer.renderPage(TextileUtil.TEXTILE_VIDEO_PAGE,
				parameters);
		return tag;
	}

}
