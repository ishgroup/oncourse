package ish.oncourse.services.textile.renderer;

import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.AttachmentTextileAttributes;
import ish.oncourse.services.textile.validator.AttachmentTextileValidator;
import ish.oncourse.util.IPageRenderer;

import java.util.HashMap;
import java.util.Map;

public class AttachmentTextileRenderer extends AbstractRenderer {

	private IPageRenderer pageRenderer;

	public AttachmentTextileRenderer(IBinaryDataService binaryDataService,
									 IPageRenderer pageRenderer) {
		this.pageRenderer = pageRenderer;
		validator = new AttachmentTextileValidator(binaryDataService);
	}

	@Override
	protected String internalRender(String tag) {
		Map<String, String> tagParams = TextileUtil.getTagParams(tag,
				AttachmentTextileAttributes.getAttrValues());
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(TextileUtil.TEXTILE_ATTACHMENT_PAGE_PARAM, tagParams);
		tag = pageRenderer.renderPage(TextileUtil.TEXTILE_ATTACHMENT_PAGE,
				parameters);
		return tag;
	}
}
