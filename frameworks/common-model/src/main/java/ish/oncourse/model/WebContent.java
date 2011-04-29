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
		WebNode node = getWebContentVisibility().getWebNode();
		if (node != null) {
			node.setModified(today);
		}
		setModified(today);
	}

	public int compareTo(WebContent content) {
		WebContentVisibility visibility = content.getWebContentVisibility();

		if (this.getWebContentVisibility() != null && visibility != null) {
			return visibility.getWeight() - this.getWebContentVisibility().getWeight();
		}

		return 0;
	}
}
