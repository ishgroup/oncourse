package ish.oncourse.util;

import java.util.Map;

public interface IPageRenderer {
	String renderPage(String pageName, Map<String, Object> parameters);
	String encodedPage(String pageName, Map<String, Object> parameters);
}
