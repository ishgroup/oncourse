package ish.oncourse.services.textile;

import java.util.HashMap;
import java.util.Map;


public class TextileUtil {

	public static final String QUOT_REGEXP = "\"|&#8220;|&#8221;";
	public static final String VIDEO_WIDTH_DEFAULT = "425";
	public static final String VIDEO_HEIGHT_DEFAULT = "344";
	public static final String DIGIT_ATTR_IN_QUOTS = "("+QUOT_REGEXP+")(\\d+)("+QUOT_REGEXP+")";
	public static final String STR_WITH_WHITESPACE= "("+QUOT_REGEXP+")((\\w|\\s)+)("+QUOT_REGEXP+")";
	public static final String STR_IN_QUOTS= "("+QUOT_REGEXP+")((\\w)+)("+QUOT_REGEXP+")";
	public static final String IMAGE_NAME_REGEXP = "\\{image name:"+STR_WITH_WHITESPACE+"}";
	public static final String IMAGE_ID_REGEXP = "\\{image id:"+DIGIT_ATTR_IN_QUOTS+"}";
	public static final String BLOCK_REGEXP = "\\{block( ((name:"+STR_WITH_WHITESPACE+")|(tag:"+STR_WITH_WHITESPACE+"))){0,2}}";
	public static final String BLOCK_TAG_REGEXP = "\\{block tag:"+STR_WITH_WHITESPACE+"}";
	public static final String VIDEO_TEMPLATE_EXP="\\{video( ((id:"+STR_IN_QUOTS+")|(type:"+STR_IN_QUOTS+")|(width:"+DIGIT_ATTR_IN_QUOTS+")|(height:"+DIGIT_ATTR_IN_QUOTS+"))){1,4}}";
	public static final String PARAM_ID="id:";
	public static final String PARAM_WIDTH = "width:";
	public static final String PARAM_HEIGHT = "height:";
	public static final String PARAM_NAME = "name:";
	public static final String VIDEO_PARAM_TYPE = "type:";
	public static final String BLOCK_PARAM_TAG = "tag:";
	
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

}
