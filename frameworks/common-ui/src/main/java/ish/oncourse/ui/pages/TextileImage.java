package ish.oncourse.ui.pages;

import java.util.Map;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.textile.TextileUtil;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class TextileImage {
	@Inject
	private Request request;
	
	@Inject
	private IBinaryDataService binaryDataService;

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
		Map<String, String> tagParams = (Map<String, String>) request
				.getAttribute("additionalImageParameters");
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

		BinaryInfo imageBinaryInfo = null;
		if (id != null) {
			imageBinaryInfo = binaryDataService.getBinaryInfo(
					BinaryInfo.REFERENCE_NUMBER_PROPERTY, Integer.valueOf(id));
		} else if (name != null) {
			imageBinaryInfo = binaryDataService.getBinaryInfo(
					BinaryInfo.NAME_PROPERTY, name);
		}
		imagePath = imageBinaryInfo != null ? ("/asset/binarydata?id=" + imageBinaryInfo
				.getReferenceNumber())
				: "";
		imageAlign = align != null ? align : "";
		imageAlt = alt != null ? alt : "";
		imageTitle = title != null ? title : "";
		imageWidth = width != null ? width : "";
		imageHeight = height != null ? height : "";
		imageClass = cssClass != null ? cssClass : "";
		imageCaption = caption;
		imageLink = link != null ? link : "";
	}

}
