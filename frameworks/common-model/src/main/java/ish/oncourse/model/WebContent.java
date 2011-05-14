package ish.oncourse.model;

import ish.oncourse.model.auto._WebContent;
import ish.oncourse.model.visitor.IVisitor;

import java.util.Date;

public class WebContent extends _WebContent implements Comparable<WebContent> {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId().getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

	public <T> T accept(IVisitor<T> visitor) {
		return visitor.visitWebContent(this);
	}

	@Override
	protected void onPostAdd() {
		Date today = new Date();

		setCreated(today);
		setModified(today);
	}

	@Override
	protected void onPreUpdate() {
		Date today = new Date();

		for (WebContentVisibility webContentVisibility : getWebContentVisibilities()) {
			WebNode node = webContentVisibility.getWebNode();
			if (node != null) {
				node.setModified(today);
			}
		}

		setModified(today);
	}

	public WebContentVisibility getWebContentVisibility(WebNodeType webNodeType) {
		for (WebContentVisibility webContentVisibility : getWebContentVisibilities()) {
			WebNodeType nodeType = webContentVisibility.getWebNodeType();
			if (nodeType != null) {
				if (nodeType.equals(webNodeType)) {
					return webContentVisibility;
				}
			}
		}
		return null;
	}

	public int compareTo(WebContent arg) {
		int result = 0;

		String name1 = getName();
		String name2 = arg.getName();

		if (name1 != null && name2 != null) {
			result = name1.compareTo(name2);
		} else {
			// first blocks with non-null names go
			if (name1 == null && name2 != null) {
				return 1;
			}
			if (name1 != null && name2 == null) {
				return -1;
			}
		}
		if (result != 0) {
			return result;
		}
		if (getContent() != null && arg.getContent() != null) {
			result = getContent().compareTo(arg.getContent());
		}
		if (result != 0) {
			return result;
		}
		result = getId().compareTo(arg.getId());
		if (result != 0) {
			return result;
		}
		return hashCode() - arg.hashCode();
	}
}
