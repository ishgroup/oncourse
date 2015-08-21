package ish.oncourse.model;

import ish.oncourse.model.auto._WebNodeType;
import ish.oncourse.model.visitor.IVisitor;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.lifecycle.cache.CacheGroups;
import org.apache.cayenne.query.*;

import java.util.Date;
import java.util.List;

import static org.apache.cayenne.query.QueryCacheStrategy.LOCAL_CACHE;

@CacheGroups(value = {"WebNodeType", "WebNode", "WebContentVisibility", "WebContent"})
public class WebNodeType extends _WebNodeType {
	private static final long serialVersionUID = -1945260610761430515L;
	public static final String PAGE = "page";
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
		q.setCacheStrategy(LOCAL_CACHE);
		q.setCacheGroups(WebNodeType.class.getSimpleName());

		q.andQualifier(ExpressionFactory.matchExp(WebNodeType.NAME_PROPERTY,
				name));
		return (WebNodeType) ctx.performQuery(q);
	}

	@SuppressWarnings("unchecked")
	public List<WebContent> getContentForRegionKey(String regionKey) {
		return ObjectSelect.query(WebContent.class)
				.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, WebContent.class.getSimpleName())
				.and(WebContent.WEB_CONTENT_VISIBILITIES.dot(WebContentVisibility.WEB_NODE_TYPE).eq(this))
				.and(WebContent.WEB_CONTENT_VISIBILITIES.dot(WebContentVisibility.REGION_KEY).eq(RegionKey.valueOf(regionKey.toLowerCase())))
				.orderBy(WebContent.WEB_CONTENT_VISIBILITIES.dot(WebContentVisibility.WEIGHT).asc()).select(getObjectContext());
	}
	
	public boolean isDefaultPageTheme(){
		return PAGE.equals(getName());
	}
	
	public boolean isThemeUsedInPages() {
		return !getWebNodes().isEmpty();
	}
}
