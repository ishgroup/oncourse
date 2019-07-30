package ish.oncourse.services.lifecycle;

import org.apache.cayenne.*;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.query.Query;
import org.apache.cayenne.query.RelationshipQuery;
import org.apache.cayenne.query.SelectQuery;

/**
 * DataChannelFilter implementation which intercepts relationship queries
 * and forces them to fetch data from db skipping object cache.
 */
public class RelationshipQueryInvalidatingFilter implements DataChannelQueryFilter {

	@Override
	public QueryResponse onQuery(ObjectContext originatingContext, Query query, DataChannelQueryFilterChain filterChain) {
		if (query instanceof RelationshipQuery) {
			ObjectId objectId = ((RelationshipQuery) query).getObjectId();
			if (objectId.isTemporary() && !objectId.isReplacementIdAttached()) {
				throw new CayenneRuntimeException("Can't build a query for relationship '"
						+ ((RelationshipQuery) query).getRelationshipName()
						+ "' for temporary id: "
						+ objectId);
			}

			ObjRelationship relationship = ((RelationshipQuery) query).getRelationship(originatingContext.getEntityResolver());

			// build executable select...
			Expression qualifier = ExpressionFactory.matchDbExp(relationship
					.getReverseDbRelationshipPath(), objectId);

			SelectQuery<Object> replacementQuery = new SelectQuery<>(
					relationship.getTargetEntity(),
					qualifier);
			replacementQuery.setStatementFetchSize(((RelationshipQuery) query).getStatementFetchSize());

			return filterChain.onQuery(originatingContext, replacementQuery);
		}

		return filterChain.onQuery(originatingContext, query);
	}
}
