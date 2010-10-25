package ish.oncourse.services.textile.renderer;

import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.validator.ImageTextileValidator;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.ValidationErrors;

import java.util.HashMap;
import java.util.Map;

public class ImageTextileRenderer extends AbstractRenderer {

	private IPageRenderer pageRenderer;

	public ImageTextileRenderer(IBinaryDataService binaryDataService, IPageRenderer pageRenderer) {
		this.pageRenderer = pageRenderer;
		validator = new ImageTextileValidator(binaryDataService);
	}

	@Override
	public String render(String tag, ValidationErrors errors) {
		tag = super.render(tag, errors);
		if (!errors.hasFailures()) {
			Map<String, String> tagParams = TextileUtil.getTagParams(tag,
					TextileUtil.PARAM_ID, TextileUtil.PARAM_NAME,
					TextileUtil.IMAGE_PARAM_ALIGH,
					TextileUtil.IMAGE_PARAM_CAPTION,
					TextileUtil.IMAGE_PARAM_ALT,
					TextileUtil.IMAGE_PARAM_LINK,
					TextileUtil.IMAGE_PARAM_TITLE, TextileUtil.PARAM_WIDTH,
					TextileUtil.PARAM_HEIGHT, TextileUtil.IMAGE_PARAM_CLASS);
			Map<String, Object> parameters=new HashMap<String, Object>();
			parameters.put("additionalImageParameters", tagParams);
			tag = pageRenderer.renderPage("ui/TextileImage", parameters);
		}
		return tag;
	}

}
