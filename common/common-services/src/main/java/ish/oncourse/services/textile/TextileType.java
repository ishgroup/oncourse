package ish.oncourse.services.textile;

import ish.oncourse.services.textile.attrs.*;
import ish.oncourse.services.textile.courseList.TextileAttrs;

import java.util.List;

import static ish.oncourse.services.textile.TextileUtil.*;

public enum TextileType {
	IMAGE("\\{image([^}]*)}", "\\{image(.+?((" +
				"name:" + STR_WHITESPACE + ")" +
				"|(id:" + DIGIT_IN_QUOTS + ")" +
			    "|(align:" + inQuots("(right|left|center)", false) + ")" +
			    "|(caption:" + STR_WHITESPACE + ")" +
			    "|(alt:" + STR_WHITESPACE + ")" +
			    "|(link:" + STR_IN_QUOTS + ")" +
			    "|(title:" + STR_WHITESPACE + ")" +
			    "|(width:" + PIXELS_IN_QUOTS + ")" +
			    "|(height:" + PIXELS_IN_QUOTS + ")" +
			    "|(class:" + STR_WHITESPACE + ")" +
				"|(attachment:" + STR_WHITESPACE + ")" +
			")){0,11}}",
			ImageTextileAttributes.getAttrValues()),

	VIDEO("\\{video([^}]*)}", "\\{video(.+?((id:" + STR_IN_QUOTS + ")" + "|(type:" + STR_IN_QUOTS + ")" + "|(width:"
			+ PIXELS_IN_QUOTS + ")" + "|(height:" + PIXELS_IN_QUOTS + "))){1,4}}", VideoTextileAttributes
			.getAttrValues()),

	BLOCK("\\{block([^}]*)}", "\\{block(.+?name:" + STR_WHITESPACE + ")?}", BlockTextileAttributes
			.getAttrValues()),

	COURSE("\\{course([\\}]|([\\ ].*))", "\\{course(.+?((code:" + STR_IN_QUOTS + ")" + "|(tag:" + STR_WHITESPACE + ")"
			+ "|(showclasses:" + BOOLEAN_IN_QUOTS + "))){0,3}}", CourseTextileAttributes.getAttrValues()),

	COURSE_LIST("\\{courses([\\}]|([\\ ].*))", "\\{courses(.+?((tag:" + STR_WHITESPACE + ")"
			+ "|(limit:" + DIGIT_IN_QUOTS+ ")"
			+ "|(sort:" + inQuots("(date|alphabetical|availability)", false) + ")"
			+ "|(order:" + inQuots("(asc|desc)", false)+ ")"
			+ "|(style:" + inQuots("(titles|details)", false) + ")"
			+ "|(showTags:" + BOOLEAN_IN_QUOTS + ")" +
			")){0,4}}", TextileAttrs.getAttrValues()),

	PAGE("\\{page([^}]*)}", "\\{page(.+?((code:" + DIGIT_IN_QUOTS + "))){0,1}}", PageTextileAttributes.getAttrValues()),

	TAGS("\\{tags([^}]*)}", "\\{tags(.+?((maxLevels:" + DIGIT_IN_QUOTS + ")"
			+ "|(showDetail:" + BOOLEAN_IN_QUOTS + ")"
			+ "|(hideTopLevel:" + BOOLEAN_IN_QUOTS + ")"
			+ "|(multiSelect:"+ BOOLEAN_IN_QUOTS + ")"
			+ "|(name:" + STR_WHITESPACE + ")" +
			")){0,4}}",
			TagsTextileAttributes.getAttrValues()), 
	TEXT("\\{text([^}]*)}","\\{text(.+?((label:" + STR_WHITESPACE + ")|(required:" + YES_NO_IN_QUOTS
					+ ")|(lines:((" + DIGIT_IN_QUOTS + ")|("+YES_NO_IN_QUOTS+")))|(maxlength:" + DIGIT_IN_QUOTS + "))){1,4}}", TextTextileAttributes.getAttrValues()),
	RADIOLIST("\\{radiolist([^}]*)}","\\{radiolist(.+?((label:" + STR_WHITESPACE + ")|(required:" + YES_NO_IN_QUOTS
							+ ")|(default:" + STR_WHITESPACE + ")|(options:" + STR_WHITESPACE + "))){1,4}}", RadioListTextileAttributes.getAttrValues()),
	POPUPLIST("\\{popuplist([^}]*)}","\\{popuplist(.+?((label:" + STR_WHITESPACE + ")|(required:" + YES_NO_IN_QUOTS
									+ ")|(default:" + STR_WHITESPACE + ")|(options:" + STR_WHITESPACE + "))){1,4}}", PopupListTextileAttributes.getAttrValues()),
	FORM("(\\{form([^}]*)}.+?((("+TEXT.regexp+")|("+RADIOLIST.regexp+")|("+POPUPLIST.regexp+")).+?)*(\\{form}))",
			"\\{form(.+?((name:" + STR_WHITESPACE + ")|(email:" + STR_IN_QUOTS
					+ ")|(url:" + STR_IN_QUOTS+ "))){2,3}}.+?((("+TEXT.detailedRegexp+")|("+RADIOLIST.detailedRegexp+")|("+POPUPLIST.detailedRegexp+")).+?)*+\\{form}", FormTextileAttributes.getAttrValues()),
	
	ATTACHMENT("\\{attachment([^}]*)}", "\\{attachment(.+?name:" + STR_WHITESPACE + ")?}", AttachmentTextileAttributes.getAttrValues()),

	LOCATION("\\{location([^}]*)}",
			"\\{location(.+?(" +
				"(display:" + STR_WHITESPACE + ")" +
				"|(suburb:" + STR_WHITESPACE + ")" +
				"|(postcode:" + DIGIT_IN_QUOTS + ")" +
				"|(distance:" + DIGIT_IN_QUOTS + ")" +
				"|(site:" + DIGIT_IN_QUOTS + ")" +
				")){0,5}}",
			LocationTextileAttribute.getAttrValues()),

	TUTORS("\\{tutors([^}]*)}", "\\{tutors(.+?((" +
			"tagName:" + STR_WHITESPACE + ")" +
			"|(id:" + DIGIT_IN_QUOTS + ")" +
			"|(count:" + DIGIT_IN_QUOTS + ")" +
			")){0,3}}",
			 TutorsTextileAttributes.getAttrValues());

	private String regexp;
	private String detailedRegexp;
	private List<String> attributes;

	private TextileType(String regexp, String detailedRegexp, List<String> attributes) {
		this.regexp = regexp;
		this.detailedRegexp = detailedRegexp;
		this.attributes = attributes;
	}

	public String getRegexp() {
		return regexp;
	}

	public String getDetailedRegexp() {
		return detailedRegexp;
	}

	public List<String> getAttributes() {
		return attributes;
	}
	
	public static final TextileType[] BASE_TYPES={IMAGE, VIDEO, BLOCK, COURSE, COURSE_LIST, PAGE, TAGS, FORM, ATTACHMENT, LOCATION, TUTORS};

	public static final String FORM_FIELDS_PATTERN="("+TEXT.regexp+")|("+RADIOLIST.regexp+")|("+POPUPLIST.regexp+")";
}
