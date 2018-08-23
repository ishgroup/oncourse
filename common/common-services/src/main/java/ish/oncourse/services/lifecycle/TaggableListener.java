package ish.oncourse.services.lifecycle;

import ish.oncourse.configuration.Configuration;
import ish.oncourse.model.College;
import ish.oncourse.model.Course;
import ish.oncourse.model.Queueable;
import ish.oncourse.model.Taggable;
import ish.oncourse.services.course.ICourseService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.solr.BuildSolrClient;
import ish.oncourse.solr.reindex.ReindexCourses;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.annotation.PostPersist;
import org.apache.cayenne.annotation.PostUpdate;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.solr.client.solrj.SolrClient;

import java.util.Collections;
import java.util.Deque;
import java.util.Map;
import java.util.WeakHashMap;

import static ish.oncourse.configuration.Configuration.AppProperty.ZK_HOST;

public class TaggableListener {

	private ICayenneService cayenneService;
	private SolrClient solrClient;
	private ICourseService courseService;
	
	public TaggableListener(ICayenneService cayenneService, ICourseService courseService) {
		this.cayenneService = cayenneService;
		this.courseService = courseService;
		String zkHost = Configuration.getValue(ZK_HOST);
		if (zkHost != null) {
			solrClient = BuildSolrClient.instance(zkHost).build();
		}
	}

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
				
				if (taggable.getEntityIdentifier().equals(Course.class.getSimpleName()) && solrClient != null) {
					new ReindexCourses(cayenneService.newNonReplicatingContext(), 
							solrClient,
							Collections.singleton(taggable.getEntityWillowId()),
							courseService::getAvailableSiteKeys).run();
				}
			}
		}
	}

}
