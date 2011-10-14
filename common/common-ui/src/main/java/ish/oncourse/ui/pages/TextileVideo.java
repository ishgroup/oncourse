package ish.oncourse.ui.pages;

import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.VideoTextileAttributes;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Map;

public class TextileVideo {

	@Inject
	private Request request;
	
	@Property
	private String videoWidth;
	
	@Property
	private String videoHeight;
	
	@Property
	private String videoYoutubeId;
	
	@SetupRender
	void beforeRender(){
		Map<String, String> tagParams = (Map<String, String>) request
		.getAttribute(TextileUtil.TEXTILE_VIDEO_PAGE_PARAM);
		
		videoYoutubeId = tagParams.get(VideoTextileAttributes.VIDEO_PARAM_ID.getValue());
		videoWidth = tagParams.get(VideoTextileAttributes.VIDEO_PARAM_WIDTH.getValue());
		videoHeight = tagParams.get(VideoTextileAttributes.VIDEO_PARAM_HEIGHT.getValue());
	}
	
}
