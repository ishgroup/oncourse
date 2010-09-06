package ish.oncourse.services.textile.renderer;

import java.util.Map;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.validator.ImageTextileValidator;
import ish.oncourse.util.ValidationErrors;

public class ImageTextileRenderer extends AbstractRenderer {

	private IBinaryDataService binaryDataService;

	public ImageTextileRenderer(IBinaryDataService binaryDataService) {
		this.binaryDataService = binaryDataService;
		validator = new ImageTextileValidator(binaryDataService);
	}

	@Override
	public String render(String tag, ValidationErrors errors) {
		tag = super.render(tag, errors);
		if (!errors.hasFailures()) {
			BinaryInfo imageBinaryInfo = null;
			Map<String, String> tagParams = TextileUtil.getTagParams(tag,
					TextileUtil.PARAM_ID, TextileUtil.PARAM_NAME,
					TextileUtil.IMAGE_PARAM_ALIGH,
					TextileUtil.IMAGE_PARAM_CAPTION,
					TextileUtil.IMAGE_PARAM_ALT,
					TextileUtil.IMAGE_PARAM_LINK,
					TextileUtil.IMAGE_PARAM_TITLE, TextileUtil.PARAM_WIDTH,
					TextileUtil.PARAM_HEIGHT, TextileUtil.IMAGE_PARAM_CLASS);
			String id = tagParams.get(TextileUtil.PARAM_ID);
			String name = tagParams.get(TextileUtil.PARAM_NAME);
			String align = tagParams.get(TextileUtil.IMAGE_PARAM_ALIGH);
			String alt = tagParams.get(TextileUtil.IMAGE_PARAM_ALT);
			String caption = tagParams.get(TextileUtil.IMAGE_PARAM_CAPTION);
			String link = tagParams.get(TextileUtil.IMAGE_PARAM_LINK);
			String title = tagParams.get(TextileUtil.IMAGE_PARAM_TITLE);
			String width = tagParams.get(TextileUtil.PARAM_WIDTH);
			String height = tagParams.get(TextileUtil.PARAM_HEIGHT);
			String cssClass = tagParams.get(TextileUtil.IMAGE_PARAM_CLASS);

			if (id != null) {
				imageBinaryInfo = binaryDataService.getBinaryInfo(
						BinaryInfo.REFERENCE_NUMBER_PROPERTY, Integer.valueOf(id));
			} else if (name != null) {
				imageBinaryInfo = binaryDataService.getBinaryInfo(
						BinaryInfo.NAME_PROPERTY, name);
			}

			String path = "/servlet/binarydata?id=" + imageBinaryInfo.getReferenceNumber();
			String additionalParams = "";
			if (align != null) {
				additionalParams += " align=\"" + align + "\" ";
			}
			if (alt != null) {
				additionalParams += " alt=\"" + alt + "\" ";
			}
			if (title != null) {
				additionalParams += " title=\"" + title + "\" ";
			}
			if (width != null) {
				additionalParams += " width=\"" + width + "\" ";
			}
			if (height != null) {
				additionalParams += " height=\"" + height + "\" ";
			}
			if (cssClass != null) {
				additionalParams += " class=\"" + cssClass + "\" ";
			}
			String img = "<img src=\"" + path + "\" " + additionalParams + "/>";
			if (caption != null) {
				img = "<div>" + img + "<br/>" + caption + "</div>";
			}
			if (link != null) {
				tag = "<a href=\"" + link + "\">" + img + "</a>";
			} else {
				tag = img;
			}
		}
		return tag;
	}

}
