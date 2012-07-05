package ish.oncourse.textile.pages;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.ImageTextileAttributes;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.ArrayList;
import java.util.Map;

public class TextileImage {
	private static final String ASSET_BINARYDATA_PATH = "/asset/binarydata?id=";

	@Inject
	private Request request;

	@Inject
	private IBinaryDataService binaryDataService;
	
	@SuppressWarnings("all")
	@Inject
	private IWebSiteService webSiteService;

	@SuppressWarnings("all")
	@Property
	private String imageLink;

	@SuppressWarnings("all")
	@Property
	private String imagePath;

	@SuppressWarnings("all")
	@Property
	private String imageAlign;

	@SuppressWarnings("all")
	@Property
	private String imageAlt;

	@SuppressWarnings("all")
	@Property
	private String imageTitle;

	@SuppressWarnings("all")
	@Property
	private String imageWidth;

	@SuppressWarnings("all")
	@Property
	private String imageHeight;

	@SuppressWarnings("all")
	@Property
	private String imageClass;

	@SuppressWarnings("all")
	@Property
	private String imageCaption;

	@SuppressWarnings("unchecked")
	@SetupRender
	void beforeRender() {
		Map<String, String> tagParams = (Map<String, String>) request.getAttribute(TextileUtil.TEXTILE_IMAGE_PAGE_PARAM);
		String id = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_ID.getValue());
		String name = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_NAME.getValue());
		String align = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_ALIGH.getValue());
		String alt = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_ALT.getValue());
		String caption = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_CAPTION.getValue());
		String link = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_LINK.getValue());
		String title = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_TITLE.getValue());
		String width = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_WIDTH.getValue());
		String height = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_HEIGHT.getValue());
		String cssClass = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_CLASS.getValue());

		BinaryInfo imageBinaryInfo = null;
		if (id != null) {
			imageBinaryInfo = binaryDataService.getBinaryInfoByReferenceNumber(id);
		} else if (name != null) {
			imageBinaryInfo = binaryDataService.getBinaryInfo(BinaryInfo.NAME_PROPERTY, name);
		} else {
			imageBinaryInfo = binaryDataService.getRandomImage();
		}

		imagePath = imageBinaryInfo != null ? (ASSET_BINARYDATA_PATH + imageBinaryInfo.getId()) : StringUtils.EMPTY;
		imageAlign = align != null ? align : StringUtils.EMPTY;
		imageAlt = alt != null ? alt : StringUtils.EMPTY;
		imageTitle = title != null ? title : StringUtils.EMPTY;
		imageWidth = width != null ? width : (imageBinaryInfo.getPixelWidth() + "px");
		imageHeight = height != null ? height : (imageBinaryInfo.getPixelHeight() + "px");
		imageClass = cssClass != null ? cssClass : StringUtils.EMPTY;
		imageCaption = caption;
		imageLink = link != null ? link : StringUtils.EMPTY;

		ArrayList<Long> ids = (ArrayList<Long>) request.getAttribute(BinaryInfo.DISPLAYED_IMAGES_IDS);
		if (ids == null) {
			ids = new ArrayList<Long>();
		}
		ids.add(imageBinaryInfo.getId());
		request.setAttribute("displayedImagesIds", ids);
	}

}
