/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.utils;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

public class JSONFormat extends Format {

	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
		String s = String.valueOf(obj);
		//escape single quote for json value
		return toAppendTo.append(escape(s));
	}

	@Override
	public Object parseObject(String source, ParsePosition pos) {
		return null;
	}


	/**
	 * this code is borrowed from json-simple:1.1.1
	 * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters (U+0000 through U+001F).
	 * @param s
	 * @return
	 */
	public static String escape(String s){
		if(s==null)
			return null;
		StringBuffer sb = new StringBuffer();
		escape(s, sb);
		return sb.toString();
	}

	/**
	 * @param s - Must not be null.
	 * @param sb
	 */
	static void escape(String s, StringBuffer sb) {
		for(int i=0;i<s.length();i++){
			char ch=s.charAt(i);
			switch(ch){
				case '\'':
					sb.append("\\\'");
					break;
				case '"':
					sb.append("\\\"");
					break;
				case '\\':
					sb.append("\\\\");
					break;
				case '\b':
					sb.append("\\b");
					break;
				case '\f':
					sb.append("\\f");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\r':
					sb.append("\\r");
					break;
				case '\t':
					sb.append("\\t");
					break;
				case '/':
					sb.append("\\/");
					break;
				default:
					//Reference: http://www.unicode.org/versions/Unicode5.1.0/
					if((ch>='\u0000' && ch<='\u001F') || (ch>='\u007F' && ch<='\u009F') || (ch>='\u2000' && ch<='\u20FF')){
						String ss=Integer.toHexString(ch);
						sb.append("\\u");
						for(int k=0;k<4-ss.length();k++){
							sb.append('0');
						}
						sb.append(ss.toUpperCase());
					}
					else{
						sb.append(ch);
					}
			}
		}
	}
}
