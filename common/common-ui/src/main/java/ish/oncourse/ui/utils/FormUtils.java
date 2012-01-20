package ish.oncourse.ui.utils;


import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;

public class FormUtils {
	
	private static final String TOKEN_ATTRIBUTE = "form_submit_token";
	
	public static void saveToken(Request request, String token) {
		Session session = request.getSession(false);
		synchronized (session) {
			session.setAttribute(TOKEN_ATTRIBUTE, token);
		}
	}
	
	public static boolean isTokenValid(Request request, String token) {
		Session session = request.getSession(false);
		synchronized (session) {
			String t = (String) session.getAttribute(TOKEN_ATTRIBUTE);
			if (t != null && token.equals(t)) {
				session.setAttribute(TOKEN_ATTRIBUTE, null);
				return true;
			}
		}
		return false;
	}
}
