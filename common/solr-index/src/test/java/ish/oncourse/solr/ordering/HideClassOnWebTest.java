/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.solr.ordering;

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import io.reactivex.Observable;
import ish.oncourse.services.courseclass.ClassAge;
import ish.oncourse.services.courseclass.ClassAgeType;
import ish.oncourse.solr.InitSolr;
import ish.oncourse.solr.model.SCourse;
import ish.oncourse.solr.query.BuildBoostQuery;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static ish.oncourse.solr.query.SolrQueryBuilder.*;

/**
 * User: akoiro
 * Date: 12/6/18
 */
@ThreadLeakScope(ThreadLeakScope.Scope.NONE)
public class HideClassOnWebTest extends SolrTestCaseJ4 {
	static {
		InitSolr.INIT_STATIC_BLOCK();
	}

	private SolrClient solrClient;
	private static InitSolr initSolr;
	private List<SCourse> courses;

	@BeforeClass
	public static void beforeClass() throws Exception {
		initSolr = InitSolr.coursesCore();
		initSolr.init();
	}


	@Before
	public void before() throws Exception {
		solrClient = new EmbeddedSolrServer(h.getCore());

		courses = new LinkedList<>();

		courses.add(new BuildSCourse().code("DAY_BEFORE")
				.id(1)
				.startDay(-1)
				.amount(3).build());

		courses.add(new BuildSCourse().code("DAY_AFTER")
				.id(2)
				.startDay(1)
				.amount(3).build());

		courses.add(new BuildSCourse().code("2DAYS_AFTER")
				.id(3)
				.startDay(2)
				.amount(3).build());

		courses.add(new BuildSCourse().code("ENDED")
				.id(4)
				.startDay(356 * 100)
				.amount(3).build());
	}

	@Test
	public void hide_day_after_start() throws IOException, SolrServerException {
		assertSolrQuery(ClassAgeType.afterClassStarts, "DAY_AFTER", "2DAYS_AFTER", "ENDED", "DAY_BEFORE");
	}


	@Test
	public void hide_day_before_start() throws IOException, SolrServerException {
		assertSolrQuery(ClassAgeType.beforeClassStarts, "2DAYS_AFTER", "ENDED", "DAY_BEFORE", "DAY_AFTER");
	}


	private void assertSolrQuery(ClassAgeType classAgeType, String... expected) throws SolrServerException, IOException {

		Throwable[] throwable = new Throwable[1];

		Observable.fromIterable(courses).blockingSubscribe((c) -> solrClient.addBean(c),
				t -> throwable[0] = t,
				() -> solrClient.commit());

		if (throwable[0] != null)
			throw new IllegalArgumentException(throwable[0]);

		SolrQuery q = new BuildBoostQuery().hideAge(ClassAge.valueOf(1, classAgeType)).build()
				.addFilterQuery("+collegeId: 1 end:[NOW TO *]")
				.setFields("id", "name", "course_code", "score", "$boostFunction", "$startDateFunction", "ms(startDate)", "$hideAge")
				.setShowDebugInfo(true)
				.addSort(new SolrQuery.SortClause(FIELD_score, SolrQuery.ORDER.desc))
				.addSort(new SolrQuery.SortClause(FIELD_startDate, SolrQuery.ORDER.asc))
				.addSort(new SolrQuery.SortClause(FIELD_name, SolrQuery.ORDER.asc));

		QueryResponse response = solrClient.query(q);
		Assert.assertEquals(expected.length, response.getResults().getNumFound());

		Observable.fromArray(expected)
				.scan(ImmutablePair.of(-1, null), (p, v) -> ImmutablePair.of(p.left + 1, v))
				.filter((p) -> p.left != -1)
				.blockingSubscribe((r) ->
						Assert.assertEquals(r.right, response.getResults().get(r.left).getFirstValue("course_code"))
				);
	}


	static class BuildSCourse {
		private Date now = new Date();

		private String code;
		private long collegeId = 1L;
		private int startDay = 0;
		private int amount = 1;
		private long id;

		public BuildSCourse id(long id) {
			this.id = id;
			return this;
		}

		public BuildSCourse code(String code) {
			this.code = code;
			return this;
		}

		public BuildSCourse collegeId(long collegeId) {
			this.collegeId = collegeId;
			return this;
		}

		public BuildSCourse startDay(int startDay) {
			this.startDay = startDay;
			return this;
		}

		public BuildSCourse amount(int amount) {
			this.amount = amount;
			return this;
		}

		public SCourse build() {
			SCourse sCourse = new SCourse();
			sCourse.setId(String.valueOf(id));
			sCourse.setCode(code);
			sCourse.setCollegeId(collegeId);
			sCourse.setStartDate(DateUtils.addDays(now, startDay));
			sCourse.setClassStart(
					Observable
							.range(0, amount)
							.scan(new LinkedList<Date>(), (l, i) -> {
								l.add(DateUtils.addDays(now, startDay + i));
								return l;
							}).lastOrError().blockingGet());

			sCourse.setClassEnd(sCourse.getClassStart().stream().map((d) -> DateUtils.addHours(d, 1)).collect(Collectors.toList()));
			return sCourse;
		}
	}
}
