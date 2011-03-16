package ish.oncourse.services.textile;

import static ish.oncourse.services.textile.TextileUtil.*;

import ish.oncourse.services.textile.attrs.BlockTextileAttributes;
import ish.oncourse.services.textile.attrs.CourseListTextileAttributes;
import ish.oncourse.services.textile.attrs.CourseTextileAttributes;
import ish.oncourse.services.textile.attrs.ImageTextileAttributes;
import ish.oncourse.services.textile.attrs.PageTextileAttributes;
import ish.oncourse.services.textile.attrs.TagsTextileAttributes;
import ish.oncourse.services.textile.attrs.VideoTextileAttributes;

import java.util.List;

public enum TextileType {
	IMAGE("\\{image([^}]*)}", "\\{image( ((name:"+STR_WHITESPACE+")" +
											"|(id:"+DIGIT_IN_QUOTS+")" +
											"|(align:"+inQuots("(right|left|center)")+")" +
											"|(caption:"+STR_WHITESPACE+")" +
											"|(alt:"+STR_WHITESPACE+")" +
											"|(link:(((\\w)|(\\W))+))" +
											"|(title:"+STR_IN_QUOTS+")" +
											"|(width:"+PIXELS_IN_QUOTS+")" +
											"|(height:"+PIXELS_IN_QUOTS+")" +
											"|(class:"+STR_IN_QUOTS+"))){1,10}}",
													ImageTextileAttributes.getAttrValues()),
											
	VIDEO("\\{video([^}]*)}", "\\{video( ((id:"+STR_IN_QUOTS+")" +
											"|(type:"+STR_IN_QUOTS+")" +
											"|(width:"+PIXELS_IN_QUOTS+")" +
											"|(height:"+PIXELS_IN_QUOTS+"))){1,4}}",
													VideoTextileAttributes.getAttrValues()),
											
	BLOCK("\\{block([^}]*)}",  "\\{block( ((name:"+STR_WHITESPACE+")" +
											"|(tag:"+STR_WHITESPACE_SLASH+"))){0,2}}",
													BlockTextileAttributes.getAttrValues()),
											
	COURSE("\\{course([^}]*)}", "\\{course( ((code:" + STR_IN_QUOTS + ")" +
											"|(tag:" + STR_WHITESPACE_SLASH + ")" +
											"|(showclasses:" + BOOLEAN_IN_QUOTS + "))){0,3}}",
													CourseTextileAttributes.getAttrValues()),
	
	COURSE_LIST("\\{courses([^}]*)}", "\\{courses( ((tag:" + STR_WHITESPACE_SLASH + ")" +
													"|(limit:" + DIGIT_IN_QUOTS+ ")" +
													"|(sort:"+inQuots("(date|alphabetical|availability)")+")" +
													"|(order:"+inQuots("(asc|desc)")+"))){0,4}}",
															CourseListTextileAttributes.getAttrValues()),
	
	PAGE("\\{page([^}]*)}", "\\{page( ((code:"+DIGIT_IN_QUOTS+"))){0,1}}", PageTextileAttributes.getAttrValues()),
	
	TAGS("\\{tags([^}]*)}", "\\{tags( ((maxLevels:"+DIGIT_IN_QUOTS+")" +
										"|(showDetail:"+BOOLEAN_IN_QUOTS+")" +
										"|(hideTopLevel:"+BOOLEAN_IN_QUOTS+")" +
										"|(name:"+STR_WHITESPACE_SLASH+"))){0,4}}",
												TagsTextileAttributes.getAttrValues());
	
	private String regexp;
	private String detailedRegexp;
	private List<String> attributes;
	
	private TextileType(String regexp, String detailedRegexp, List<String> attributes) {
		this.regexp = regexp;
		this.detailedRegexp = detailedRegexp;
		this.attributes = attributes;
	}
	
	public String getRegexp(){
		return regexp;
	}
	
	public String getDetailedRegexp(){
		return detailedRegexp;
	}

	public List<String> getAttributes() {
		return attributes;
	}

}
