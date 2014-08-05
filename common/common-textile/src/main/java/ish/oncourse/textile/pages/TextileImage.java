package ish.oncourse.textile.pages;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.Document;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.ImageTextileAttributes;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Map;

public class TextileImage {
	@Inject
	private Request request;

	@Inject
	private IBinaryDataService binaryDataService;

	@Inject
	private IWebSiteService webSiteService;

	@Property
	private String imageLink;

	@Property
	private String imagePath;

	@Property
	private String imageAlign;

	@Property
	private String imageAlt;

	@Property
	private String imageTitle;

	@Property
	private String imageWidth;

	@Property
	private String imageHeight;

	@Property
	private String imageClass;

	@Property
	private String imageCaption;

	@SetupRender
	void beforeRender() {

		Map<String, String> tagParams = (Map<String, String>) request.getAttribute(TextileUtil.TEXTILE_IMAGE_PAGE_PARAM);
		String name = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_NAME.getValue());
		String align = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_ALIGH.getValue());
		String alt = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_ALT.getValue());
		String caption = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_CAPTION.getValue());
		String link = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_LINK.getValue());
		String title = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_TITLE.getValue());
		String width = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_WIDTH.getValue());
		String height = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_HEIGHT.getValue());
		String cssClass = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_CLASS.getValue());
		String attachment = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_ATTACHMENT.getValue());

		Document imageBinaryInfo = getBinaryInfoBy(name);

		imagePath = imageBinaryInfo != null ? binaryDataService.getUrl(imageBinaryInfo) : StringUtils.EMPTY;
		imageAlign = align != null ? align : StringUtils.EMPTY;
		imageAlt = alt != null ? alt : StringUtils.EMPTY;
		imageTitle = title != null ? title : StringUtils.EMPTY;
		imageWidth = width != null ? width : (imageBinaryInfo.getCurrentVersion().getPixelWidth() + "px");
		imageHeight = height != null ? height : (imageBinaryInfo.getCurrentVersion().getPixelHeight() + "px");
		imageClass = cssClass != null ? cssClass : StringUtils.EMPTY;
		imageCaption = caption;
		if (link != null)
			imageLink = link;
		else if (attachment != null)
		{
			Document attachmentBinaryInfo = getBinaryInfoBy(attachment);
			imageLink = attachmentBinaryInfo != null ? 
					binaryDataService.getUrl(attachmentBinaryInfo) : StringUtils.EMPTY;
		}
		else
			imageLink = StringUtils.EMPTY;
	}

	private Document getBinaryInfoBy(String name) {
		Document imageBinaryInfo = null;
		if (name != null) {
			imageBinaryInfo = binaryDataService.getBinaryInfo(BinaryInfo.NAME_PROPERTY, name);
		}
		
		return imageBinaryInfo;
	}
}
