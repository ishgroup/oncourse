/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.reference.updater;

import ish.oncourse.webservices.util.GenericReferenceStub;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

/**
 */
public abstract class BaseReferenceUpdater<T extends GenericReferenceStub> implements IReferenceUpdater<T> {

	protected ObjectContext ctx;

	/**
	 * @param ctx
	 */
	public BaseReferenceUpdater(ObjectContext ctx) {
		this.ctx = ctx;
	}

	protected <T> T findOrCreateEntity(Class<T> clazz, Long willowId) {
		var entity = findEntity(clazz, willowId);
		return entity == null ? this.ctx.newObject(clazz) : entity;
	}

	protected <T> T findEntity(Class<T> clazz, Long willowId) {
		var q = SelectQuery.query(clazz);
		q.andQualifier(ExpressionFactory.matchExp("willowId", willowId));
		var list = this.ctx.select(q);
		return list.isEmpty() ? null : list.get(0);
	}

}
