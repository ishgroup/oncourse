package ish.oncourse.services.lifecycle;

import ish.oncourse.model.BinaryInfoRelation;
import ish.oncourse.model.Queueable;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.annotation.PostPersist;
import org.apache.cayenne.annotation.PostUpdate;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;

public class BinaryInfoRelationListener {

	private IWebSiteService webSiteService;

	public BinaryInfoRelationListener(IWebSiteService webSiteService) {
		this.webSiteService = webSiteService;
	}

	@PostPersist(value = BinaryInfoRelation.class)
	public void postPersist(BinaryInfoRelation relation) {
		setEntityWillowId(relation);
	}

	@PostUpdate(value = BinaryInfoRelation.class)
	public void postUpdate(BinaryInfoRelation relation) {
		setEntityWillowId(relation);
	}

	private void setEntityWillowId(BinaryInfoRelation relation) {
		if (relation.getEntityWillowId() == null && relation.getEntityAngelId() != null) {

			ObjectContext objectContext = relation.getObjectContext();
			@SuppressWarnings("unchecked")
			Class<? extends Queueable> entityClass = (Class<? extends Queueable>) objectContext.getEntityResolver()
					.getObjEntity(relation.getEntityIdentifier()).getJavaClass();

			Expression expr = ExpressionFactory.matchDbExp("angelId", relation.getEntityAngelId()).andExp(
					ExpressionFactory.matchExp("college", webSiteService.getCurrentCollege()));

			ObjectSelect<? extends Queueable> q = ObjectSelect.query(entityClass, expr);

			Queueable object = (Queueable) Cayenne.objectForQuery(objectContext, q);

			if (object != null) {
				relation.setEntityWillowId(object.getId());
				objectContext.commitChanges();
			}
		}
	}
}
