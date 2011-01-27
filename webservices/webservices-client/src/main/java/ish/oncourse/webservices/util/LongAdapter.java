package ish.oncourse.webservices.util;

import javax.xml.bind.DatatypeConverter;

public class LongAdapter {
	public static Long parseLong(String s) {
		if (s == null) {
			return null;
		}
		return DatatypeConverter.parseLong(s);
	}

	public static String printLong(Long s) {
		return DatatypeConverter.printLong(s);
	}
}
