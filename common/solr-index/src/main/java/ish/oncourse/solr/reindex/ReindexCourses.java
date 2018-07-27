package ish.oncourse.solr.reindex;

import io.reactivex.schedulers.Schedulers;
import ish.oncourse.solr.SolrCollection;
import ish.oncourse.solr.functions.course.CourseFunctions;
import ish.oncourse.solr.functions.course.SCourseFunctions;
import ish.oncourse.solr.model.SCourse;
import org.apache.cayenne.ObjectContext;
import org.apache.solr.client.solrj.SolrClient;

import java.util.Date;
import java.util.Set;

public class ReindexCourses extends ReindexCollection<SCourse> {
	
	public ReindexCourses(ObjectContext objectContext, SolrClient solrClient) {
		super(solrClient,
				SolrCollection.courses,
				SCourseFunctions.SCourses(objectContext,
						new Date(),
						Schedulers.io(), () -> CourseFunctions.Courses.call(objectContext)));
	}

	public ReindexCourses(ObjectContext objectContext, SolrClient solrClient, Set<Long> ids) {
		super(solrClient,
				SolrCollection.courses,
				SCourseFunctions.SCourses(objectContext,
						new Date(),
						Schedulers.io(), () -> CourseFunctions.CoursesById.call(objectContext, ids)));
	}
}