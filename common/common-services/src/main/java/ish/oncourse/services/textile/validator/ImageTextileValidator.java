package ish.oncourse.services.textile.validator;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.filestorage.IFileStorageAssetService;
import ish.oncourse.services.textile.TextileType;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.ImageTextileAttributes;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationFailureType;
import org.apache.commons.lang.StringUtils;

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
		BinaryInfo result = null;

		Map<String, String> tagParams = TextileUtil.getTagParams(tag, textileType.getAttributes());
		String id = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_ID.getValue());
		String name = tagParams.get(ImageTextileAttributes.IMAGE_PARAM_NAME.getValue());
		if (id != null) {
			if (!StringUtils.isNumeric(id))
			{
				errors.addFailure(getWorngIdMessage(id), ValidationFailureType.SYNTAX);
				return;
			}
			Integer idNum = Integer.valueOf(id);
			logger.warn(String.format("Found a ussage of image textile with id param = %s instead of name param. Update the textile to load image by name.",
				idNum));
			result = binaryDataService.getBinaryInfoByReferenceNumber(idNum);
			if (result == null) {
				errors.addFailure(getNotFoundByIdMessage(idNum), ValidationFailureType.CONTENT_NOT_FOUND);
			}

		} else if (name != null) {
			result = binaryDataService.getBinaryInfo(BinaryInfo.NAME_PROPERTY, name);
			if (result == null) {
				errors.addFailure(getNotFoundByNameMessage(name), ValidationFailureType.CONTENT_NOT_FOUND);
			}
		}
	}

	/**
	 * @param tag
	 * @return
	 */
	public String getRequiredAttrsMessage(String tag) {
		return "The image: " + tag + " must contain at least one of the required attributes: name or id";
	}

	public String getFormatErrorMessage(String tag) {
		return "The image tag '" + tag + "' doesn't match {image id:\"id\" name:\"name\" align:\"right|left|center\" "
				+ "caption:\"your caption\" alt:\"your alt\" link:\"link\" "
				+ "title:\"title\" width:\"digit\" height:\"digit\" class:\"cssClass\"}";
	}

	public String getNotFoundByIdMessage(Integer id) {
		return "There's no image with the id: " + id;
	}

	public String getNotFoundByNameMessage(String name) {
		return "There's no image with the name: " + name;
	}

	public String getNotFoundContentMessage() {
		return "This image's content is missed";
	}

	public String getWorngIdMessage(String id)
	{
		return String.format("Wrong id \"%s\".",id);
	}

}
