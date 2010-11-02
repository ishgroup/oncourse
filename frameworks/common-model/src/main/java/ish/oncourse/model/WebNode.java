package ish.oncourse.model;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import ish.oncourse.model.auto._WebNode;

public class WebNode extends _WebNode {

	static final String DEFAULT_PAGE_TITLE = "New Page";

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

	public String getPath() {
		WebUrlAlias defaultAlias = getDefaultWebURLAlias();

		String alias = defaultAlias == null ? ("/page/" + getNodeNumber())
				: defaultAlias.getUrlPath();
		return alias;
	}

	public String getUrlShortName() {
		String s = getName();
		if (s == null) {
			s = DEFAULT_PAGE_TITLE;
		}
		return s.trim().replaceAll(" ", "+").replaceAll("/", "|");
	}

	@Override
	protected void onPostAdd() {
		Date today = new Date();
		setCreated(today);
		setModified(today);
		setPublished(false);
	}

	public WebUrlAlias getDefaultWebURLAlias() {
		Expression expr = ExpressionFactory.matchExp(
				WebUrlAlias.DEFAULT_PROPERTY, true);

		List<WebUrlAlias> l = expr.filterObjects(getWebUrlAliases());

		if (l.size() > 0) {
			return l.get(0);
		} else if (getWebUrlAliases().size() > 0) {
			return getWebUrlAliases().get(0);
		} else {
			return null;
		}
	}
	
	public void clearDefaultUrl() {
		for (WebUrlAlias alias : getWebUrlAliases()) {
			alias.setDefault(false);
		}
	}
}
