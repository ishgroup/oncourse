package ish.oncourse.model;

import ish.oncourse.model.auto._WebContent;
import ish.oncourse.model.visitor.IVisitor;

import java.util.Date;

public class WebContent extends _WebContent {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
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
			// for unassigned blocks: if looking for webContentVisibility
			// without webNodeType assigned
			if (webNodeType == null) {
				return webContentVisibility;
			}
		}
		return null;
	}
}
