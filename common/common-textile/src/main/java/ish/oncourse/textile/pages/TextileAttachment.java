package ish.oncourse.textile.pages;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.AttachmentTextileAttributes;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Map;

public class TextileAttachment {
	
	@Inject
	private Request request;
	
	@Inject
	private IBinaryDataService binaryDataService;
	
    @Property
    private BinaryInfo attachment;
	
	@SetupRender
	void beforeRender() {
		@SuppressWarnings("unchecked")
		Map<String, String> tagParams = (Map<String, String>) request.getAttribute(TextileUtil.TEXTILE_ATTACHMENT_PAGE_PARAM);
		
		String attachmentName = tagParams.get(AttachmentTextileAttributes.ATTACHMENT_PARAM_NAME.getValue());

        attachment = binaryDataService.getBinaryInfo(BinaryInfo.NAME_PROPERTY, attachmentName);
	}
	
	public String getAttachmentUrl() {
		return binaryDataService.getUrl(attachment);
	}

}
