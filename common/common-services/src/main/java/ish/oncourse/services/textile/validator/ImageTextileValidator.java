package ish.oncourse.services.textile.validator;

import ish.oncourse.model.Document;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.filestorage.IFileStorageAssetService;
import ish.oncourse.services.textile.TextileType;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.ImageTextileAttributes;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationFailureType;

import java.util.Map;

public class ImageTextileValidator extends AbstractTextileValidator {

	private IBinaryDataService binaryDataService;
    private IFileStorageAssetService fileStorageAssetService;

	public ImageTextileValidator(IBinaryDataService binaryDataService, IFileStorageAssetService fileStorageAssetService) {
		this.binaryDataService = binaryDataService;
        this.fileStorageAssetService = fileStorageAssetService;
    }

	@Override
	protected void initValidator() {
		textileType = TextileType.IMAGE;
	}

	@Override
	protected void specificTextileValidate(String tag, ValidationErrors errors) {
		Document result = null;

		Map<String, String> tagParams = TextileUtil.getTagParams(tag, textileType.getAttributes());
		String name = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_NAME.getValue());
		if (name != null) {
			result = binaryDataService.getBinaryInfo(Document.NAME_PROPERTY, name);
			if (result == null) {
				errors.addFailure(getNotFoundByNameMessage(name), ValidationFailureType.CONTENT_NOT_FOUND);
			}
		}
		else
		{
			errors.addFailure(getRequiredAttrsMessage(tag), ValidationFailureType.SYNTAX);
		}
	}

	/**
	 * @param tag
	 * @return
	 */
	public String getRequiredAttrsMessage(String tag) {
		return "The image: " + tag + " must contain the required attribute: name";
	}

	public String getFormatErrorMessage(String tag) {
		return "The image tag '" + tag + "' doesn't match {image name:\"name\" align:\"right|left|center\" "
				+ "caption:\"your caption\" alt:\"your alt\" link:\"link\" "
				+ "title:\"title\" width:\"digit\" height:\"digit\" class:\"cssClass\"}";
	}

	public String getNotFoundByNameMessage(String name) {
		return "There's no image with the name: " + name;
	}

	public String getNotFoundContentMessage() {
		return "This image's content is missed";
	}
}
