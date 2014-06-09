package ish.oncourse.model;

import ish.oncourse.model.auto._WebContent;
import ish.oncourse.model.visitor.IVisitor;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;

public class WebContent extends _WebContent implements Comparable<WebContent> {
	
	/**
	 * SerialUID
	 */
	private static final long serialVersionUID = -900137336888575297L;
	
	/**
	 * Logger
	 */
	private static final Logger LOG = Logger.getLogger(WebContent.class);

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

	/**
	 * Search for visibility which corresponds to webNodeType
	 * @param webNodeType web node type
	 * @return visibility
	 */
	public WebContentVisibility getWebContentVisibility(WebNodeType webNodeType) {
		LOG.debug(String.format("Searching webVisibility for webContent:%s with webNodeType:%s", this.getId(), webNodeType.getId()));
		SelectQuery q = new SelectQuery(WebContentVisibility.class);
		q.andQualifier(ExpressionFactory.matchExp(WebContentVisibility.WEB_CONTENT_PROPERTY, this));
		q.andQualifier(ExpressionFactory.matchExp(WebContentVisibility.WEB_NODE_TYPE_PROPERTY, webNodeType));
		@SuppressWarnings("unchecked")
		List<WebContentVisibility> list = getObjectContext().performQuery(q);
		LOG.debug(String.format("The number of found visibilities: %s", list.size()));
		return list.isEmpty() ? null : list.get(0);

	}
	
	/**
	 * Search for visibity which corresponds to webNode.
	 * @param webNode web node
	 * @return visibility
	 */
	public WebContentVisibility getWebContentVisibility(WebNode webNode) {
		LOG.debug(String.format("Searching webVisibility for webNode:%s and webContent:%s", webNode.getId(), this.getId()));
		SelectQuery q = new SelectQuery(WebContentVisibility.class);
		q.andQualifier(ExpressionFactory.matchExp(WebContentVisibility.WEB_NODE_PROPERTY, webNode));
		q.andQualifier(ExpressionFactory.matchExp(WebContentVisibility.WEB_CONTENT_PROPERTY, this));
		@SuppressWarnings("unchecked")
		List<WebContentVisibility> list = getObjectContext().performQuery(q);
		LOG.debug(String.format("The number of found visibilities: %s", list.size()));
		
		return list.isEmpty() ? null : list.get(0);
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
