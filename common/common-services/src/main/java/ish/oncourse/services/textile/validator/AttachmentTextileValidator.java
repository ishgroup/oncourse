package ish.oncourse.services.textile.validator;

import ish.oncourse.model.Document;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.textile.TextileType;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.AttachmentTextileAttributes;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationFailureType;

import java.util.Map;

public class AttachmentTextileValidator extends AbstractTextileValidator {
	
	private IBinaryDataService binaryDataService;

    public AttachmentTextileValidator(IBinaryDataService binaryDataService) {
		this.binaryDataService = binaryDataService;
    }
	
	@Override
	protected void initValidator() {
		textileType = TextileType.ATTACHMENT;
	}
	
	@Override
	protected void specificTextileValidate(String tag, ValidationErrors errors) {
		Document result;
		
		Map<String, String> tagParams = TextileUtil.getTagParams(tag, textileType.getAttributes());
		String name = tagParams.get(AttachmentTextileAttributes.ATTACHMENT_PARAM_NAME.getValue());
		
		if (name != null) {
			result = binaryDataService.getBinaryInfo(Document.NAME_PROPERTY, name);
			if (result == null) {
				errors.addFailure(getNotFoundMessage(name), ValidationFailureType.CONTENT_NOT_FOUND);
			}
		}
	}
	
	public String getNotFoundMessage(String name) {
		return "There's no attachment with the name: " + name;
	}
	
	public String getNotFoundContentMessage() {
		return "This attachment's content is missed";
	}

	@Override
	public String getFormatErrorMessage(String tag) {
		return "The attachment tag '" + tag + "' doesn't match {attachment name:\"name\"}";
	}

}
