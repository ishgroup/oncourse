package ish.oncourse.services.lifecycle;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.annotation.PrePersist;
import org.apache.cayenne.annotation.PreUpdate;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.Taggable;
import ish.oncourse.services.site.IWebSiteService;

public class TaggableListener {

	private IWebSiteService webSiteService;

	public TaggableListener(IWebSiteService webSiteService) {
		this.webSiteService = webSiteService;
	}

	@PrePersist(value = Taggable.class)
	public void prePersist(Taggable taggable) {
		setEntityWillowId(taggable);
	}

	@PreUpdate(value = Taggable.class)
	public void preUpdate(Taggable taggable) {
		setEntityWillowId(taggable);
	}

	private void setEntityWillowId(Taggable taggable) {
		if (taggable.getEntityWillowId() == null && taggable.getEntityAngelId() != null) {

			ObjectContext objectContext = taggable.getObjectContext();
			@SuppressWarnings("unchecked")
			Class<? extends Queueable> entityClass = (Class<? extends Queueable>) objectContext.getEntityResolver()
					.getObjEntity(taggable.getEntityIdentifier()).getJavaClass();

			Expression expr = ExpressionFactory.matchDbExp("angelId", taggable.getEntityAngelId()).andExp(
					ExpressionFactory.matchExp("college", webSiteService.getCurrentCollege()));

			SelectQuery q = new SelectQuery(entityClass, expr);

			Queueable object = (Queueable) Cayenne.objectForQuery(objectContext, q);

			if (object != null) {
				taggable.setEntityWillowId(object.getId());
			}
		}
	}

}
