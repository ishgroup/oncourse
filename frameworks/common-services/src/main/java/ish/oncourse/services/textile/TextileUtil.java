package ish.oncourse.services.textile;

import ish.oncourse.util.ValidationErrors;

import java.util.HashMap;
import java.util.Map;


public class TextileUtil {

	public static final String QUOT_REGEXP = "\"|&#8220;|&#8221;";
	public static final String DIGIT_ATTR_IN_QUOTS = "("+QUOT_REGEXP+")(\\d+)("+QUOT_REGEXP+")";
	public static final String STR_WITH_WHITESPACE= "("+QUOT_REGEXP+")((\\w|\\s)+)("+QUOT_REGEXP+")";
	public static final String STR_IN_QUOTS= "("+QUOT_REGEXP+")((\\w)+)("+QUOT_REGEXP+")";
	
	public static final String IMAGE_REGEXP = "\\{image( ((name:"+STR_WITH_WHITESPACE+")|(id:"
												+DIGIT_ATTR_IN_QUOTS+")|(align:("
												+QUOT_REGEXP+")(right|left|center)("+QUOT_REGEXP+"))|(caption:"
												+STR_WITH_WHITESPACE+")|(link:"
												+"(((\\w)|(\\W))+))|(title:"
												+STR_IN_QUOTS+")|(width:"
												+DIGIT_ATTR_IN_QUOTS+")|(height:"
												+DIGIT_ATTR_IN_QUOTS+")|(class:"+STR_IN_QUOTS+"))){1,9}}";
	
	public static final String BLOCK_REGEXP = "\\{block( ((name:"+STR_WITH_WHITESPACE+")|(tag:"+STR_WITH_WHITESPACE+"))){0,2}}";
	public static final String VIDEO_TEMPLATE_EXP="\\{video( ((id:"+STR_IN_QUOTS+")|(type:"+STR_IN_QUOTS+")|(width:"+DIGIT_ATTR_IN_QUOTS+")|(height:"+DIGIT_ATTR_IN_QUOTS+"))){1,4}}";
	public static final String COURSE_REGEXP = "\\{course( ((code:"+STR_IN_QUOTS+")|(tag:"+STR_WITH_WHITESPACE+"))){0,2}}";
	
	public static final String PARAM_ID="id:";
	public static final String PARAM_WIDTH = "width:";
	public static final String PARAM_HEIGHT = "height:";
	public static final String PARAM_NAME = "name:";
	public static final String PARAM_TAG = "tag:";
	
	public static final String VIDEO_PARAM_TYPE = "type:";
	
	public static final String IMAGE_PARAM_ALIGH = "align:";
	public static final String IMAGE_PARAM_CAPTION = "caption:";
	public static final String IMAGE_PARAM_LINK = "link:";
	public static final String IMAGE_PARAM_CLASS = "class:";
	public static final String IMAGE_PARAM_TITLE = "title:";
	
	public static final String COURSE_PARAM_CODE = "code:";
	
	public static final String VIDEO_WIDTH_DEFAULT = "425";
	public static final String VIDEO_HEIGHT_DEFAULT = "344";
	
	/**
	 * @param tag
	 * @return
	 */
	public static String getValueInFirstQuots(String tag) {
		return tag.split(QUOT_REGEXP)[1];
	}
	
	public static Map<String, String> getTagParams(String tag, String... paramKeys){
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
			ValidationErrors errors, String... params) {
		for (String param : params) {
			if (!isUniqueParam(tag, param)) {
				errors.addFailure("The tag: " + tag
						+ " can't contain more than one \""
						+ param.replace(":", "") + "\" attribute");
			}
		}
	}
	
	public static void checkRequiredParams(String tag,
			ValidationErrors errors, String... params) {
		for (String param : params) {
			if (!hasRequiredParam(tag, param)) {
				errors.addFailure("The tag: " + tag
						+ " doesn't have the required \""
						+ param.replace(":", "") + "\" attribute");
			}
		}
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
