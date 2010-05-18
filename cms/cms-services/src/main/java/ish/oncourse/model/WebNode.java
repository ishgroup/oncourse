package ish.oncourse.model;

import ish.oncourse.model.auto._WebNode;

public class WebNode extends _WebNode {

	static final String DEFAULT_PAGE_TITLE = "New Page";

	public String getPath() {
		if (getParent() == null || getParent() == getSite().getHomePage()) {
			return "/" + getUrlShortName();
		}

		return getParent().getPath() + "/" + getUrlShortName();
	}

	public String getUrlShortName() {
		String s = getShortName();
		if (s == null) {
			s = getName();
		}
		if (s == null) {
			s = DEFAULT_PAGE_TITLE;
		}
		return s.trim().replaceAll("\\s", "+");
	}
}
