package ish.oncourse.model;

import ish.oncourse.model.auto._WebContent;
import ish.oncourse.model.visitor.IVisitor;

import java.util.Date;

public class WebContent extends _WebContent implements Comparable<WebContent> {

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
		WebNode node = getWebContentVisibility().getWebNode();
		if (node != null) {
			node.setModified(today);
		}
		setModified(today);
	}

	public int compareTo(WebContent content) {
		WebContentVisibility visibility = content.getWebContentVisibility();
		int result = 0;
		if (this.getWebContentVisibility() != null && visibility != null) {
			result = visibility.getWeight()
					- this.getWebContentVisibility().getWeight();
		}
		if (result != 0) {
			return result;
		}
		if (getName() != null && content.getName() != null) {
			result = getName().compareTo(content.getName());
		}
		if (result != 0) {
			return result;
		}
		if (getContent() != null && content.getContent() != null) {
			result = getContent().compareTo(content.getContent());
		}
		if (result != 0) {
			return result;
		}
		result = getId().compareTo(content.getId());
		if (result != 0) {
			return result;
		}
		return hashCode() - content.hashCode();
	}
}
