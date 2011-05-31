package ish.oncourse.model;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import ish.oncourse.model.auto._BinaryInfoRelation;

public class BinaryInfoRelation extends _BinaryInfoRelation implements Queueable {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId().getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

	@Override
	protected void onPrePersist() {
		if (getEntityWillowId() == null && getEntityAngelId() != null) {
			
			@SuppressWarnings("unchecked")
			Class<? extends Queueable> entityClass = (Class<? extends Queueable>) objectContext.getEntityResolver()
					.getObjEntity(getEntityIdentifier()).getJavaClass();
			
			Expression expr = ExpressionFactory.matchDbExp("angelId", getEntityAngelId());
			
			SelectQuery q = new SelectQuery(entityClass, expr);
			
			Queueable object = (Queueable) Cayenne.objectForQuery(getObjectContext(), q);
			
			if (object != null) {
				setEntityWillowId(object.getId());
			}
		}
	}

	@Override
	protected void onPreUpdate() {
		onPrePersist();
	}
}
