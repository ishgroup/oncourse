package ish.oncourse.solr.query;

import ish.oncourse.solr.BuildSolrClient;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class ClassesSolrQueryTest {

	private static Logger logger = LogManager.getLogger();

	private static String TIME_PARAMETER_VALUE_EVENING = "evening";

	/**
	 * Query example:
	 *
	 * q	"(courseId:(503434 503205 672619))^=0 ((price:[0 TO 1500.0]) AND (when:Tuesday) AND (when:evening) AND ({!geofilt pt=-33.896449,151.180013 sfield=classLoc d=0.08445175}) AND (siteId:493061) AND (tutorId:302) AND (start:[2019-02-14T10:51:09Z TO 2019-03-15T10:51:09Z]))^=1.0"
	 * qt	"edismax"
	 * pt	"-33.896449,151.180013"
	 * group.limit	"1000"
	 * fl	"score dist:geodist() *"
	 * start	"0"
	 * fq	"courseId:(503434 503205 672619)"
	 * sfield	"classLoc"
	 * rows	"2147483647"
	 * group.field	"courseId"
	 * group	"true"
	 */
	@Test
	public void buildClassesSolrQueryTest() throws IOException, SolrServerException {

		SearchParams searchParams = new SearchParams();
		searchParams.setDay(DayOption.tues);
		searchParams.setTime(TIME_PARAMETER_VALUE_EVENING);
		searchParams.setPrice(1500D);
		searchParams.addSuburb(Suburb.valueOf("2042", -33.896449, 151.180013, 0.08445175));
		searchParams.setAfter(DateUtils.addDays(new Date(), 1));
		searchParams.setBefore(DateUtils.addDays(new Date(), 30));
		searchParams.setTutorId(302L);
		searchParams.setSiteId(493061L);

		Set<Long> coursesIds = new HashSet<Long>(){{add(503434L); add(503205L); add(672619L);}};

		SolrQuery classesQuery = ClassesQueryBuilder.valueOf(searchParams, coursesIds)
				.addFieldList("* score")
				.enableDistanceField()
				.enableGrouping()
				//.enableExplain()
				//.enableDebug()
				.build();

		List<String> fields = Arrays.asList(classesQuery.getFields().split(" "));
		Assert.assertTrue(fields.contains("*"));
		Assert.assertTrue(fields.contains("score"));

		SolrClient solrClient = BuildSolrClient.instance("127.0.0.1:2181/solr").build();
		QueryResponse classes = solrClient.query("classes", classesQuery, SolrRequest.METHOD.POST);

		ClassesGroupResponseParser parser = ClassesGroupResponseParser.valueOf(classes.getGroupResponse(), "courseId");
		Set<String> groupValues = parser.getGroupValues();
		groupValues.forEach(groupValue -> {
			SolrDocumentList groupDocumentList = parser.getGroupDocumentList(groupValue);
			Assert.assertTrue(!getContent(groupDocumentList).isEmpty());
			//logger.info(getContent(groupDocumentList));
		});

		//logger.info(classesQuery.toString());
	}

	private String getContent(SolrDocumentList groupDocumentList) {
		return (String)groupDocumentList.get(0).get("content");
	}
}
