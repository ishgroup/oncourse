/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.lifecycle;

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
			var objectId = ((RelationshipQuery) query).getObjectId();
			if (objectId.isTemporary() && !objectId.isReplacementIdAttached()) {
				throw new CayenneRuntimeException("Can't build a query for relationship '"
						+ ((RelationshipQuery) query).getRelationshipName()
						+ "' for temporary id: "
						+ objectId);
			}

			var relationship = ((RelationshipQuery) query).getRelationship(originatingContext.getEntityResolver());

			// build executable select...
			var qualifier = ExpressionFactory.matchDbExp(relationship
					.getReverseDbRelationshipPath(), objectId);

			var replacementQuery = new SelectQuery<>(
                    relationship.getTargetEntity(),
                    qualifier);
			replacementQuery.setStatementFetchSize(((RelationshipQuery) query).getStatementFetchSize());

			return filterChain.onQuery(originatingContext, replacementQuery);
		}

		return filterChain.onQuery(originatingContext, query);
	}
}
