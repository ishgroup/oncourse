package ish.oncourse.webservices.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceUtils {
	public static String stackTraceAsString(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
