package ish.oncourse.model;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;

import ish.oncourse.model.auto._WebNodeType;
import ish.oncourse.model.visitor.IVisitor;

public class WebNodeType extends _WebNodeType {

	public static final String PAGE = "Page";
	public static final String DEFAULT_LAYOUT_KEY = "default";

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}
		
	@Override
	protected void onPostAdd() {
		Date today = new Date();
		setModified(today);
		setCreated(today);
	}

	public <T> T accept(IVisitor<T> visitor) {
		return visitor.visitWebNodeType(this);
	}

	public static WebNodeType forName(ObjectContext ctx, String name) {
		SelectQuery q = new SelectQuery(WebNodeType.class);
		q.andQualifier(ExpressionFactory.matchExp(WebNodeType.NAME_PROPERTY,
				name));
		return (WebNodeType) DataObjectUtils.objectForQuery(ctx, q);
	}

	public List<WebContent> getContentForRegionKey(String regionKey) {
		SelectQuery q = new SelectQuery(WebContent.class);

		q.andQualifier(ExpressionFactory.matchExp(
				WebContent.WEB_CONTENT_VISIBILITY_PROPERTY + "."
						+ WebContentVisibility.WEB_NODE_TYPE_PROPERTY, this));

		q.andQualifier(ExpressionFactory.matchExp(
				WebContent.WEB_CONTENT_VISIBILITY_PROPERTY + "."
						+ WebContentVisibility.REGION_KEY_PROPERTY, RegionKey.valueOf(regionKey.toLowerCase())));

		q.addOrdering(new Ordering(WebContent.WEB_CONTENT_VISIBILITY_PROPERTY
				+ "." + WebContentVisibility.WEIGHT_PROPERTY,
				SortOrder.ASCENDING));

		return getObjectContext().performQuery(q);
	}
}
