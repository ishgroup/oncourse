package ish.oncourse.textile.pages;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.AttachmentTextileAttributes;

import java.util.Map;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class TextileAttachment {
	
	@Inject
	private Request request;
	
	@Inject
	private IBinaryDataService binaryDataService;
	
	@SuppressWarnings("all")
	@Property
	private String attachmentLink;
	
	@SuppressWarnings("all")
	@Property
	private String attachmentTitle;
	
	@Property
	private String attachmentName;
	
	@SetupRender
	void beforeRender() {
		@SuppressWarnings("unchecked")
		Map<String, String> tagParams = (Map<String, String>) request.getAttribute(TextileUtil.TEXTILE_ATTACHMENT_PAGE_PARAM);
		
		attachmentName = tagParams.get(AttachmentTextileAttributes.ATTACHMENT_PARAM_NAME.getValue());
		
		BinaryInfo binaryInfo = binaryDataService.getBinaryInfo(BinaryInfo.NAME_PROPERTY, attachmentName);
		
		if (binaryInfo != null) {
			attachmentLink = binaryInfo.getId() != null ? "/asset/binarydata?id=" + binaryInfo.getId() : "";
			
			String size = binaryInfo.getByteSize() != null ? String.valueOf(binaryInfo.getByteSize() / 1024) + " kB" : "";
			String type = binaryInfo.getMimeType() != null ? binaryInfo.getMimeType() : "";
			
			attachmentTitle = attachmentName + " " + size + " " + type;
		}
	}

}
