package ish.oncourse.model;

import ish.oncourse.model.auto._WebContent;
import ish.oncourse.model.visitor.IVisitor;
import ish.oncourse.utils.QueueableObjectUtils;

import java.util.Date;

public class WebContent extends _WebContent implements Comparable<WebContent> {
	private static final long serialVersionUID = -900137336888575297L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
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

	public WebContentVisibility getWebContentVisibility(WebNode webNode, WebNodeType webNodeType) {
		for (WebContentVisibility webContentVisibility : getWebContentVisibilities()) {
			WebNode node = webContentVisibility.getWebNode();
			WebNodeType nodeType = webContentVisibility.getWebNodeType();
			if (node != null && webNode != null) {
				if (node.getId().equals(webNode.getId())) {
					return webContentVisibility;
				}
			} else if (nodeType != null && webNodeType != null) {
				if (nodeType.getId().equals(webNodeType.getId())) {
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
