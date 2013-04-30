package ish.oncourse.services.textile;

import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationFailureType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextileUtil {
    /**
     * an user can define custom template file name for any component or page
     */
    public static final String CUSTOM_TEMPLATE_DEFINITION = "customTemplateDefinition";


    public static final String TEXTILE_REGEXP = "(\\{((block)|(course)|(courses)|(tags)|(page)|(video)|(image)|(attachment))([^}]*)})|(\\{form([^}]*)}.+?(((\\{text([^}]*)})|(\\{radiolist([^}]*)})|(\\{popuplist([^}]*)})).+?)*(\\{form}))";
	public static final String QUOT = "\"|&#8220;|&#8221;|\u201C|\u201D";
	public static final String BOOLEAN_IN_QUOTS = inQuots("(true|false)", false);
	public static final String YES_NO_IN_QUOTS = inQuots("(yes|no|true|false)", false);
	public static final String PIXELS_IN_QUOTS = inQuots("(\\d+)(px)?", false);
	public static final String DIGIT_IN_QUOTS = inQuots("(\\d+)", false);
	public static final String STR_WHITESPACE = inQuots(".+", true);
	public static final String STR_IN_QUOTS = inQuots("\\S+", true);

	public static final String VIDEO_WIDTH_DEFAULT = "425";
	public static final String VIDEO_HEIGHT_DEFAULT = "344";

	public static final String TEXTILE_COURSE_PAGE = "textile/TextileCourse";
	public static final String TEXTILE_COURSE_LIST_PAGE = "textile/TextileCourseList";
	public static final String TEXTILE_IMAGE_PAGE = "textile/TextileImage";
	public static final String TEXTILE_PAGE_PAGE = "textile/TextilePage";
	public static final String TEXTILE_TAGS_PAGE = "textile/TextileTags";
	public static final String TEXTILE_VIDEO_PAGE = "textile/TextileVideo";
	public static final String TEXTILE_FORM_PAGE = "textile/TextileForm";
	public static final String TEXTILE_ATTACHMENT_PAGE = "textile/TextileAttachment";

	public static final String TEXTILE_COURSE_PAGE_PARAM = "course";
	public static final String TEXTILE_COURSE_SHOW_CLASSES_PARAM = "showclasses";
	public static final String TEXTILE_COURSE_LIST_PAGE_PARAM = "courseList";

	public static final String TEXTILE_IMAGE_PAGE_PARAM = "additionalImageParameters";

	public static final String TEXTILE_TAGS_PAGE_DETAILS_PARAM = "textileTagsShowDetails";
	public static final String TEXTILE_TAGS_PAGE_MAX_LEVEL_PARAM = "textileTagsMaxLevels";
	public static final String TEXTILE_TAGS_PAGE_HIDE_TOP_PARAM = "textileTagsHideTopLevel";
	public static final String TEXTILE_TAGS_PAGE_ROOT_TAG_PARAM = "textileTags";

	public static final String TEXTILE_VIDEO_PAGE_PARAM = "videoParameters";

	public static final String TEXTILE_FORM_PAGE_NAME_PARAM = "formName";
	public static final String TEXTILE_FORM_PAGE_EMAIL_PARAM = "email";
	public static final String TEXTILE_FORM_PAGE_FIELDS_PARAM = "fields";
	public static final String TEXTILE_FORM_PAGE_AFTER_FIELDS_PARAM = "after_mark-up";
	public static final String TEXTILE_FORM_PAGE_URL_PARAM = "url";
	
	public static final String TEXTILE_ATTACHMENT_PAGE_PARAM = "attachmentParameters";

	public static final String TEXTILE_UNEXPECTED_ERROR_PARAM = "textileUnexpectedError";


	public static String inQuots(String param, boolean quotsRequired) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("(").append(QUOT).append(")");
		if (!quotsRequired) {
			buffer.append("?");
		}
		buffer.append(param).append("(").append(QUOT).append(")");
		if (!quotsRequired) {
			buffer.append("?");
		}
		return buffer.toString();
	}

	/**
	 * @param tag
	 * @return
	 */
	public static String getValue(String tag, boolean isInQuots) {
		String separator = null;
		if (isInQuots) {
			separator = QUOT;
		} else {
			separator = "[:]|\\s+|[}]";
		}
		String[] splitted = tag.split(separator);
		return splitted.length >= 2 ? splitted[1] : null;
	}

	public static Map<String, String> getTagParams(String tag, List<String> paramKeys) {
		Map<String, String> params = new HashMap<>();
		for (String key : paramKeys) {
			if (tag.contains(key)) {
				String rest = tag.substring(tag.indexOf(key));
				boolean isInQuots = false;

				if (rest.startsWith(key+"\"") || rest.startsWith(key+"&#8220;")|| rest.startsWith(key+"\u201C")) {
					isInQuots = true;
				}
				params.put(key, getValue(rest, isInQuots));
			}
		}
		return params;
	}

	/**
	 * Returns false in there are more than one param in tag, true otherwise(for
	 * non-required params)
	 * 
	 * @param tag
	 * @param param
	 * @return
	 */
	public static boolean isUniqueParam(String tag, String param) {
		int splittedByAttr = tag.split(param).length;
		if (splittedByAttr > 2) {
			return false;
		}
		return true;
	}

	public static void checkParamsUniquence(String tag, ValidationErrors errors, List<String> params) {
		for (String param : params) {
			if (!isUniqueParam(tag, param)) {
				errors.addFailure(getDoubledParamErrorMessage(tag, param), ValidationFailureType.SYNTAX);
			}
		}
	}

	/**
	 * @param tag
	 * @param param
	 * @return
	 */
	public static String getDoubledParamErrorMessage(String tag, String param) {
		return "The rich text: " + tag + " can't contain more than one \"" + param.replace(":", "") + "\" attribute";
	}

	public static void checkRequiredParams(String tag, ValidationErrors errors, String... params) {
		for (String param : params) {
			if (!hasRequiredParam(tag, param)) {
				errors.addFailure(getRequiredParamErrorMessage(tag, param), ValidationFailureType.SYNTAX);
			}
		}
	}

	public static String getRequiredParamErrorMessage(String tag, String param) {
		return "The rich text: " + tag + " doesn't have the required \"" + param.replace(":", "") + "\" attribute";
	}

	/**
	 * Returns false in there no param in tag, true otherwise
	 * 
	 * @param tag
	 * @param param
	 * @return
	 */
	public static boolean hasRequiredParam(String tag, String param) {
		int splittedByAttr = tag.split(param).length;
		if (splittedByAttr < 2) {
			return false;
		}
		return true;
	}

	public static String getReplacementForSyntaxErrorTag(String tag) {
		return "<!-- ERROR in " + tag + ". Syntax error --!> ";
	}

}
