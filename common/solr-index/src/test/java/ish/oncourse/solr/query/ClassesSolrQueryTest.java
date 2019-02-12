package ish.oncourse.solr.query;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ClassesSolrQueryTest {

	private static Logger logger = LogManager.getLogger();

	private static String TIME_PARAMETER_VALUE_EVENING = "evening";

	@Test
	public void buildClassesSolrQueryTest() {

		SearchParams searchParams = new SearchParams();
		searchParams.setDay(DayOption.tues);
		searchParams.setTime(TIME_PARAMETER_VALUE_EVENING);
		searchParams.setPrice(500D);
		searchParams.addSuburb(Suburb.valueOf("2042", -33.896449, 151.180013, 0.08445175));
		searchParams.setAfter(DateUtils.addDays(new Date(), 10));
		searchParams.setBefore(DateUtils.addDays(new Date(), 10));
		searchParams.setTutorId(302L);
		searchParams.setSiteId(493061L);

		Set<String> coursesIds = new HashSet<String>(){{add("503434"); add("503205"); add("672619");}};

		/* suburb NEWTOWN
		 * "id":"2042NEWTOWN",
		 * "suburb":"NEWTOWN",
		 * "doctype":"suburb",
		 * "state":"NSW",
		 * "postcode":"2042",
		 * "loc":"-33.896449,151.180013",
		 */


		SolrQuery classesQuery = ClassesQueryBuilder.valueOf(searchParams, coursesIds).build();

		logger.warn(classesQuery.toQueryString());

		String expectedQuery = "&debug=all" +
				"&q=" + //main query
					"start:[NOW%20TO%202019-02-18T14:30:59Z]^=0.2" +
					" siteId:493061^=0.2" +
					" ({!geofilt%20pt=-33.896449,151.180013%20sfield=classLoc%20d=1000.0})^=0.2" +
					" when:Tuesday^=0.2" +
					" when:evening^=0.2" +
					" tutorId:(2512%203662)^=0.2" +
					" price:[0.0%20TO%201000.0]^=0.2" +
				"&start=0" +
				"&rows=2147483647" +
				"&fq=" + //filter query
					"courseId:(503434%20503205%20672619)" +
					" AND" +
					" siteKey:cce-main" +
					" AND" +
					" start:[*%20TO%202019-12-31T15:15:00Z]" +
				"&fl=*%20score%20[explain]%20dist:{!func}geodist()" +
				"&sfield=classLoc" +
				"&pt=-33.896449,151.180013";

		Assert.assertEquals(expectedQuery, classesQuery.toQueryString());
	}
}
