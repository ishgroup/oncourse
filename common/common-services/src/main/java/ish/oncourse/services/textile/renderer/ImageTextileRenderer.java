package ish.oncourse.services.textile.renderer;

import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.filestorage.IFileStorageAssetService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.ImageTextileAttributes;
import ish.oncourse.services.textile.validator.ImageTextileValidator;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.ValidationErrors;

import java.util.HashMap;
import java.util.Map;

public class ImageTextileRenderer extends AbstractRenderer {

	private IPageRenderer pageRenderer;

	public ImageTextileRenderer(IBinaryDataService binaryDataService, IFileStorageAssetService fileStorageAssetService,
			IPageRenderer pageRenderer) {
		this.pageRenderer = pageRenderer;
		validator = new ImageTextileValidator(binaryDataService, fileStorageAssetService);
	}

	@Override
	public String render(String tag, ValidationErrors errors) {
		try {
			tag = super.render(tag, errors);
			if (!errors.hasFailures()) {
				Map<String, String> tagParams = TextileUtil.getTagParams(tag,
						ImageTextileAttributes.getAttrValues());
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put(TextileUtil.TEXTILE_IMAGE_PAGE_PARAM, tagParams);
				tag = pageRenderer.renderPage(TextileUtil.TEXTILE_IMAGE_PAGE,
						parameters);
			}
		} catch (Exception e) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put(TextileUtil.TEXTILE_UNEXPECTED_ERROR_PARAM, e.getMessage());
			tag = pageRenderer.renderPage(TextileUtil.TEXTILE_IMAGE_PAGE,
					parameters);
		}
		return tag;
	}

}
