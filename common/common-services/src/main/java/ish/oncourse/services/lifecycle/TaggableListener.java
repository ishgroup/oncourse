package ish.oncourse.services.lifecycle;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.Taggable;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.annotation.PostPersist;
import org.apache.cayenne.annotation.PostUpdate;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

public class TaggableListener {

	@PostPersist(value = Taggable.class)
	public void postPersist(Taggable taggable) {
		setEntityWillowId(taggable);
		setEntityAngelId(taggable);
	}
	
	@PostUpdate(value = Taggable.class)
	public void postUpdate(Taggable taggable) {
		setEntityWillowId(taggable);
		setEntityAngelId(taggable);
	}

	private void setEntityAngelId(Taggable taggable) {
		if (taggable.getEntityWillowId() != null && taggable.getEntityAngelId() == null) {
			ObjectContext objectContext = taggable.getObjectContext();
			@SuppressWarnings("unchecked")
			Class<? extends Queueable> entityClass = (Class<? extends Queueable>) objectContext.getEntityResolver()
					.getObjEntity(taggable.getEntityIdentifier()).getJavaClass();
			Expression expr = ExpressionFactory.matchDbExp("id", taggable.getEntityWillowId()).andExp(
					ExpressionFactory.matchExp("college", taggable.getCollege()));
			SelectQuery q = new SelectQuery(entityClass, expr);
			Queueable object = (Queueable) Cayenne.objectForQuery(objectContext, q);
			if (object != null) {
				taggable.setEntityAngelId(object.getAngelId());
				objectContext.commitChanges();
			}
		}
	}

	private void setEntityWillowId(Taggable taggable) {
		if (taggable.getEntityWillowId() == null && taggable.getEntityAngelId() != null) {

			ObjectContext objectContext = taggable.getObjectContext();
			@SuppressWarnings("unchecked")
			Class<? extends Queueable> entityClass = (Class<? extends Queueable>) objectContext.getEntityResolver()
					.getObjEntity(taggable.getEntityIdentifier()).getJavaClass();

			Expression expr = ExpressionFactory.matchDbExp("angelId", taggable.getEntityAngelId()).andExp(
					ExpressionFactory.matchExp("college", taggable.getCollege()));

			SelectQuery q = new SelectQuery(entityClass, expr);
			Queueable object = (Queueable) Cayenne.objectForQuery(objectContext, q);

			if (object != null) {
				taggable.setEntityWillowId(object.getId());
				objectContext.commitChanges();
			}
		}
	}

}
