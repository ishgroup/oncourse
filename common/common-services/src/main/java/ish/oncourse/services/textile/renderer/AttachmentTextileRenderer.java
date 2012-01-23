package ish.oncourse.services.textile.renderer;

import java.util.HashMap;
import java.util.Map;

import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.AttachmentTextileAttributes;
import ish.oncourse.services.textile.validator.AttachmentTextileValidator;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.ValidationErrors;

public class AttachmentTextileRenderer extends AbstractRenderer {
	
	private IPageRenderer pageRenderer;
	
	public AttachmentTextileRenderer(IBinaryDataService binaryDataService, IPageRenderer pageRenderer) {
		this.pageRenderer = pageRenderer;
		validator = new AttachmentTextileValidator(binaryDataService);
	}
	
	@Override
	public String render(String tag, ValidationErrors errors) {
		tag = super.render(tag, errors);
		if (!errors.hasFailures()) {
			Map<String, String> tagParams = TextileUtil.getTagParams(tag,
					AttachmentTextileAttributes.getAttrValues());
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(TextileUtil.TEXTILE_ATTACHMENT_PAGE_PARAM, tagParams);
			tag = pageRenderer.renderPage(TextileUtil.TEXTILE_ATTACHMENT_PAGE,
					parameters);
		}
		return tag;
	}

}
