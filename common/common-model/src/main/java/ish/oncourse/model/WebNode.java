package ish.oncourse.model;

import ish.oncourse.model.auto._WebNode;
import ish.oncourse.model.visitor.IVisitor;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

public class WebNode extends _WebNode {
	
	private static final long serialVersionUID = -221961704091301435L;
	
	static final String DEFAULT_PAGE_TITLE = "New Page";
	
	public static final String LOADED_NODE = "loaded node";	
	public static final String ADD_NEW_PAGE_ATTR = "add_new_page_attribute";
	
	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

	public <T> T accept(IVisitor<T> visitor) {
		return visitor.visitWebNode(this);
	}

	public String getPath() {
		WebUrlAlias defaultAlias = getDefaultWebURLAlias();

		String alias = defaultAlias == null ? ("/page/" + getNodeNumber()) : defaultAlias
				.getUrlPath();
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

	@Override
	protected void onPreUpdate() {
		Date today = new Date();
		setModified(today);
	}

	@Override
	public void removeFromWebUrlAliases(WebUrlAlias obj) {
		super.removeFromWebUrlAliases(obj);
		getObjectContext().deleteObject(obj);
	}

	public WebUrlAlias getWebUrlAliasByPath(String urlPath) {
		if (urlPath == null) {
			return null;
		}
		for (WebUrlAlias alias : getWebUrlAliases()) {
			if (urlPath.equals(alias.getUrlPath())) {
				return alias;
			}
		}
		return null;
	}

    public String getNameWithoutSpaces()
    {
        String name  = getName();
        return StringUtils.remove(name, " ");
    }
}
