package ish.oncourse.ui.pages;

import java.util.Map;

import ish.oncourse.services.textile.TextileUtil;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

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
		
		videoYoutubeId = tagParams.get(TextileUtil.PARAM_ID);
		videoWidth = tagParams.get(TextileUtil.PARAM_WIDTH);
		videoHeight = tagParams.get(TextileUtil.PARAM_HEIGHT);
	}
	
}
