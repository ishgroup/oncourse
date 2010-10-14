package ish.oncourse.model;

import java.util.List;

import ish.oncourse.model.auto._WebNode;

public class WebNode extends _WebNode {

	static final String DEFAULT_PAGE_TITLE = "New Page";

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN)
				: null;
	}

	public String getPath() {
		// TODO get the main of node url aliases
		List<WebUrlAlias> webUrlAliases = getWebUrlAliases();
		String alias = "page/" + getNodeNumber();
		return webUrlAliases.isEmpty() ? alias : webUrlAliases.get(0)
				.getUrlPath();
	}

	public String getUrlShortName() {
		String s = getName();
		if (s == null) {
			s = DEFAULT_PAGE_TITLE;
		}
		return s.trim().replaceAll(" ", "+").replaceAll("/", "|");
	}

	@Override
	protected void performInitialization() {}
	
}
