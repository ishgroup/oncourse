package ish.oncourse.services.textile;

import ish.oncourse.util.ValidationErrors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TextileUtil {
	public static final String TEXTILE_REGEXP = "[{]((block)|(course)|(tags)|(page)|(video)|(image))([^}]*)[}]";
	public static final String QUOT_REGEXP = "\"|&#8220;|&#8221;|Ò|Ó";
	public static final String BOOLEAN_IN_QUOTS = "("+QUOT_REGEXP+")(true|false)("+QUOT_REGEXP+")";
	public static final String PIXELS_ATTR_IN_QUOTS = "("+QUOT_REGEXP+")(\\d+)(px)?("+QUOT_REGEXP+")";
	public static final String DIGIT_IN_QUOTS = "("+QUOT_REGEXP+")(\\d+)("+QUOT_REGEXP+")";
	public static final String STR_WITH_WHITESPACE= "("+QUOT_REGEXP+")((\\w|\\s)+)("+QUOT_REGEXP+")";
	public static final String STR_IN_QUOTS= "("+QUOT_REGEXP+")((\\w)+)("+QUOT_REGEXP+")";
	
	public static final String IMAGE_REGEXP = "\\{image( ((name:"+STR_WITH_WHITESPACE+")|(id:"
												+DIGIT_IN_QUOTS+")|(align:("
												+QUOT_REGEXP+")(right|left|center)("+QUOT_REGEXP+"))|(caption:"
												+STR_WITH_WHITESPACE+")|(alt:"
												+STR_WITH_WHITESPACE+")|(link:"
												+"(((\\w)|(\\W))+))|(title:"
												+STR_IN_QUOTS+")|(width:"
												+PIXELS_ATTR_IN_QUOTS+")|(height:"
												+PIXELS_ATTR_IN_QUOTS+")|(class:"+STR_IN_QUOTS+"))){1,10}}";
	
	public static final String BLOCK_REGEXP = "\\{block( ((name:"+STR_WITH_WHITESPACE+")|(tag:"+STR_WITH_WHITESPACE+"))){0,2}}";
	public static final String VIDEO_REGEXP="\\{video( ((id:"+STR_IN_QUOTS+")|(type:"+STR_IN_QUOTS+")|(width:"+PIXELS_ATTR_IN_QUOTS+")|(height:"+PIXELS_ATTR_IN_QUOTS+"))){1,4}}";
	public static final String COURSE_REGEXP = "\\{course( ((code:" + STR_IN_QUOTS + ")|" +
															"(tag:" + STR_WITH_WHITESPACE + ")|" +
															"(enrollable:" + BOOLEAN_IN_QUOTS + ")|" +
															"(currentsearch:"+BOOLEAN_IN_QUOTS+"))){0,4}}";
	public static final String PAGE_REGEXP = "\\{page( ((code:"+DIGIT_IN_QUOTS+"))){0,1}}";
	public static final String TAGS_REGEXP="\\{tags( ((entityType:"+STR_WITH_WHITESPACE+")|(maxLevels:"
													+DIGIT_IN_QUOTS+")|(showtopdetail:"
													+BOOLEAN_IN_QUOTS+")|(isHidingTopLevelTags:"
													+BOOLEAN_IN_QUOTS+")|(isFiltered:"
													+BOOLEAN_IN_QUOTS+")|(name:"+STR_WITH_WHITESPACE+"))){0,6}}";
	
	public static final String VIDEO_WIDTH_DEFAULT = "425";
	public static final String VIDEO_HEIGHT_DEFAULT = "344";
	
	public static final String TEXTILE_COURSE_PAGE = "ui/TextileCourse";
	public static final String TEXTILE_IMAGE_PAGE = "ui/TextileImage";
	public static final String TEXTILE_PAGE_PAGE = "ui/TextilePage";
	public static final String TEXTILE_TAGS_PAGE = "ui/TextileTags";
	public static final String TEXTILE_VIDEO_PAGE = "ui/TextileVideo";
	
	public static final String TEXTILE_COURSE_PAGE_PARAM = "course";
	public static final String TEXTILE_IMAGE_PAGE_PARAM = "additionalImageParameters";
	public static final String TEXTILE_TAGS_PAGE_DETAILS_PARAM = "textileTagsShowDetails";
	public static final String TEXTILE_TAGS_PAGE_ENTITY_PARAM = "textileTagsEntityType";
	public static final String TEXTILE_TAGS_PAGE_TAGS_PARAM = "textileTags";
	public static final String TEXTILE_VIDEO_PAGE_PARAM = "videoParameters";
	
	/**
	 * @param tag
	 * @return
	 */
	public static String getValueInFirstQuots(String tag) {
		return tag.split(QUOT_REGEXP)[1];
	}
	
	public static Map<String, String> getTagParams(String tag, List<String>paramKeys){
		Map<String, String> params= new HashMap<String, String>();
		for(String key:paramKeys){
			if(tag.contains(key)){
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
				errors.addFailure(getDoubledParamErrorMessage(tag, param));
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
				errors.addFailure(getRequiredParamErrorMessage(tag, param));
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
