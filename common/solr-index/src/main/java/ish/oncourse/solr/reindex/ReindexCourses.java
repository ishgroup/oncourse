package ish.oncourse.solr.reindex;

import io.reactivex.schedulers.Schedulers;
import ish.oncourse.model.Course;
import ish.oncourse.solr.SolrCollection;
import ish.oncourse.solr.functions.course.CourseFunctions;
import ish.oncourse.solr.functions.course.SCourseFunctions;
import ish.oncourse.solr.model.SCourse;
import ish.oncourse.solr.model.SCourseClass;
import org.apache.cayenne.ObjectContext;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;

import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class ReindexCourses extends ReindexCollection<SCourse> {
	private boolean reindexClasses = true;

	public ReindexCourses(ObjectContext objectContext, SolrClient solrClient) {
		this(objectContext, solrClient, s -> new String[0]);
		reindexClasses = false;
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

	public UpdateResponse addBean(SCourse sCourse) throws IOException, SolrServerException {
		total.incrementAndGet();
		if (reindexClasses) {
			solrClient.deleteByQuery(SolrCollection.classes.name(), String.format("courseId:%s", sCourse.getId()));

			for (SCourseClass c : sCourse.getClasses()) {
				solrClient.addBean(SolrCollection.classes.name(), c);
			}
		}
		return solrClient.addBean(SolrCollection.courses.name(), sCourse);
	}

	protected void commit() throws IOException, SolrServerException {
		super.commit();
		if (reindexClasses) {
			solrClient.commit(SolrCollection.classes.name());
		}
	}
}