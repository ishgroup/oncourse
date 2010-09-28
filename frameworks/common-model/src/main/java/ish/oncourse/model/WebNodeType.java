package ish.oncourse.model;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import ish.oncourse.model.auto._WebNodeType;

public class WebNodeType extends _WebNodeType {
	
	public static final String PAGE = "Page";
	
	public static WebNodeType forName(ObjectContext ctx, String name) {
		SelectQuery q = new SelectQuery(WebNodeType.class);
		q.andQualifier(ExpressionFactory.matchExp(WebNodeType.NAME_PROPERTY,
				name));
		return (WebNodeType) DataObjectUtils.objectForQuery(ctx, q);
	}
}
