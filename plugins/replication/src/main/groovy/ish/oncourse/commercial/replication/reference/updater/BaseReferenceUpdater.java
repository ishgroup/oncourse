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
