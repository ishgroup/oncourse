package ish.oncourse.services.textile;

import ish.oncourse.util.ValidationErrors;
import ish.oncourse.util.ValidationFailureType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TextileUtil {
	public static final String TEXTILE_REGEXP = "[{]((block)|(course)|(courses)|(tags)|(page)|(video)|(image))([^}]*)[}]";
	public static final String QUOT = "\"|&#8220;|&#8221;";
	public static final String BOOLEAN_IN_QUOTS = inQuots("(true|false)");
	public static final String PIXELS_IN_QUOTS = inQuots("(\\d+)(px)?");
	public static final String DIGIT_IN_QUOTS = inQuots("(\\d+)");
	public static final String STR_WHITESPACE= inQuots("((\\w|\\s)+)");
	public static final String STR_WHITESPACE_SLASH= inQuots("((\\w|\\s|/)+)");
	public static final String STR_IN_QUOTS= inQuots("((\\w)+)");
	
	public static final String IMAGE_REGEXP = "\\{image( ((name:"+STR_WHITESPACE+")|(id:"
												+DIGIT_IN_QUOTS+")|(align:"+inQuots("(right|left|center)")+")|(caption:"
												+STR_WHITESPACE+")|(alt:"
												+STR_WHITESPACE+")|(link:"
												+"(((\\w)|(\\W))+))|(title:"
												+STR_IN_QUOTS+")|(width:"
												+PIXELS_IN_QUOTS+")|(height:"
												+PIXELS_IN_QUOTS+")|(class:"+STR_IN_QUOTS+"))){1,10}}";
	
	public static final String BLOCK_REGEXP = "\\{block( ((name:"+STR_WHITESPACE+")|(tag:"+STR_WHITESPACE_SLASH+"))){0,2}}";
	public static final String VIDEO_REGEXP="\\{video( ((id:"+STR_IN_QUOTS+")|(type:"+STR_IN_QUOTS+")|(width:"+PIXELS_IN_QUOTS+")|(height:"+PIXELS_IN_QUOTS+"))){1,4}}";
	public static final String COURSE_REGEXP = "\\{course( ((code:" + STR_IN_QUOTS + ")|" +
															"(tag:" + STR_WHITESPACE_SLASH + ")|" +
															"(showclasses:" + BOOLEAN_IN_QUOTS + "))){0,3}}";
	public static final String COURSE_LIST_REGEXP = "\\{courses( ((tag:" + STR_WHITESPACE_SLASH + ")|" +
																"(limit:" + DIGIT_IN_QUOTS+ ")|" +
																"(sort:"+inQuots("(date|alphabetical|availability)")+")|" +
																"(order:"+inQuots("(asc|desc)")+"))){0,4}}";
	public static final String PAGE_REGEXP = "\\{page( ((code:"+DIGIT_IN_QUOTS+"))){0,1}}";
	public static final String TAGS_REGEXP="\\{tags( ((maxLevels:"
													+DIGIT_IN_QUOTS+")|(showDetail:"
													+BOOLEAN_IN_QUOTS+")|(hideTopLevel:"
													+BOOLEAN_IN_QUOTS+")|(name:"+STR_WHITESPACE_SLASH+"))){0,4}}";
	
	public static final String VIDEO_WIDTH_DEFAULT = "425";
	public static final String VIDEO_HEIGHT_DEFAULT = "344";
	
	public static final String TEXTILE_COURSE_PAGE = "ui/TextileCourse";
	public static final String TEXTILE_COURSE_LIST_PAGE = "ui/TextileCourseList";
	public static final String TEXTILE_IMAGE_PAGE = "ui/TextileImage";
	public static final String TEXTILE_PAGE_PAGE = "ui/TextilePage";
	public static final String TEXTILE_TAGS_PAGE = "ui/TextileTags";
	public static final String TEXTILE_VIDEO_PAGE = "ui/TextileVideo";
	
	public static final String TEXTILE_COURSE_PAGE_PARAM = "course";
	public static final String TEXTILE_COURSE_SHOW_CLASSES_PARAM = "showclasses";
	public static final String TEXTILE_COURSE_LIST_PAGE_PARAM = "courseList";
	
	public static final String TEXTILE_IMAGE_PAGE_PARAM = "additionalImageParameters";
	
	public static final String TEXTILE_TAGS_PAGE_DETAILS_PARAM = "textileTagsShowDetails";
	public static final String TEXTILE_TAGS_PAGE_MAX_LEVEL_PARAM ="textileTagsMaxLevels";
	public static final String TEXTILE_TAGS_PAGE_HIDE_TOP_PARAM="textileTagsHideTopLevel";
	
	public static final String TEXTILE_TAGS_PAGE_ROOT_TAG_PARAM = "textileTags";
	public static final String TEXTILE_VIDEO_PAGE_PARAM = "videoParameters";
	
	private static String inQuots(String param) {
		return "(" + QUOT + ")" + param + "(" + QUOT + ")";
	}

	/**
	 * @param tag
	 * @return
	 */
	public static String getValueInFirstQuots(String tag) {
		String[] splitted = tag.split(QUOT);
		return splitted.length > 2 ? splitted[1] : null;
	}

	public static Map<String, String> getTagParams(String tag, List<String> paramKeys) {
		Map<String, String> params = new HashMap<String, String>();
		for (String key : paramKeys) {
			if (tag.contains(key)) {
				params.put(key, getValueInFirstQuots(tag.substring(tag.indexOf(key))));
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

	public static void checkParamsUniquence(String tag,
			ValidationErrors errors, List<String> params) {
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
		return "The tag: " + tag
				+ " can't contain more than one \""
				+ param.replace(":", "") + "\" attribute";
	}
	
	public static void checkRequiredParams(String tag,
			ValidationErrors errors, String... params) {
		for (String param : params) {
			if (!hasRequiredParam(tag, param)) {
				errors.addFailure(getRequiredParamErrorMessage(tag, param), ValidationFailureType.SYNTAX);
			}
		}
	}

	public static String getRequiredParamErrorMessage(String tag, String param) {
		return "The tag: " + tag
				+ " doesn't have the required \""
				+ param.replace(":", "") + "\" attribute";
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
}
