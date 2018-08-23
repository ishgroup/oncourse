package ish.oncourse.solr.reindex;

import io.reactivex.schedulers.Schedulers;
import ish.oncourse.model.Course;
import ish.oncourse.solr.SolrCollection;
import ish.oncourse.solr.functions.course.CourseFunctions;
import ish.oncourse.solr.functions.course.SCourseFunctions;
import ish.oncourse.solr.model.SCourse;
import org.apache.cayenne.ObjectContext;
import org.apache.solr.client.solrj.SolrClient;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class ReindexCourses extends ReindexCollection<SCourse> {


	public ReindexCourses(ObjectContext objectContext, SolrClient solrClient) {
		this(objectContext, solrClient, s -> new String[0]);
	}
	
	
	public ReindexCourses(ObjectContext objectContext, SolrClient solrClient, Function<Course, String[]> availableSites) {
		super(solrClient,
				SolrCollection.courses,
				SCourseFunctions.SCourses(
						new Date(),
						Schedulers.io(), () -> CourseFunctions.Courses.call(objectContext), availableSites));
	}

	public ReindexCourses(ObjectContext objectContext, SolrClient solrClient, Set<Long> ids, Function<Course, String[]> availableSites) {
		super(solrClient,
				SolrCollection.courses,
				SCourseFunctions.SCourses(
						new Date(),
						Schedulers.io(), () -> CourseFunctions.CoursesById.call(objectContext, ids), availableSites));
	}
}