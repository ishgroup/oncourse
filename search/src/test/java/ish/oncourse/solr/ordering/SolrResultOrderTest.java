package ish.oncourse.solr.ordering;

import ish.oncourse.services.search.SearchParams;
import ish.oncourse.services.search.SolrQueryBuilder;
import ish.oncourse.solr.InitSolr;
import ish.oncourse.solr.model.SolrCourse;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SolrResultOrderTest extends SolrTestCaseJ4 {
	
	private static InitSolr initSolr;
	
	private static final Long collegeId = 666L;
	private static Date startDate;

	@BeforeClass
	public static void beforeClass() throws Exception {
		initSolr = InitSolr.coursesCore();
		initSolr.init();
		
		startDate = DateUtils.addDays(new Date(), 15);
	}

	@Test
	public void test1() throws IOException, SolrServerException {
		SolrClient solrClient = new EmbeddedSolrServer(h.getCore());
		
		List<SolrCourse> preparedData = getPreparedData1();
		for (SolrCourse solrCourse : preparedData) {
			solrClient.addBean(solrCourse);
		}
		
		solrClient.commit();

		List<SolrCourse> allCourses = solrClient.query("courses", new SolrQuery("*:*")).getBeans(SolrCourse.class);
		assertEquals(7, allCourses.size());

		List<SolrCourse> courses = solrClient.query("courses", getSolrQuery()).getBeans(SolrCourse.class);
		
		assertEquals(3, courses.size());
		assertEquals("18", courses.get(0).getId());
		assertEquals("15", courses.get(1).getId());
		assertEquals("14", courses.get(2).getId());
	}

	@Test
	public void test2() throws IOException, SolrServerException {
		SolrClient solrClient = new EmbeddedSolrServer(h.getCore());

		List<SolrCourse> preparedData = getPreparedData2();
		for (SolrCourse solrCourse : preparedData) {
			solrClient.addBean(solrCourse);
		}

		solrClient.commit();

		List<SolrCourse> allCourses = solrClient.query("courses", new SolrQuery("*:*")).getBeans(SolrCourse.class);
		assertEquals(7, allCourses.size());

		List<SolrCourse> courses = solrClient.query("courses", getSolrQuery()).getBeans(SolrCourse.class);

		for (SolrCourse course : courses) {
			System.out.println(course.getId());
		}
				
		assertEquals(6, courses.size());
		assertEquals("13", courses.get(0).getId());
		assertEquals("14", courses.get(1).getId());
		assertEquals("15", courses.get(2).getId());
		assertEquals("16", courses.get(3).getId());
		assertEquals("18", courses.get(4).getId());
		assertEquals("17", courses.get(5).getId());
	}
	
	
	private static SolrQuery getSolrQuery() {
		SearchParams searchParams = new SearchParams();
		searchParams.setS("aged care");
		searchParams = SearchParams.valueOf(searchParams, true);
		
		SolrQueryBuilder builder = SolrQueryBuilder.valueOf(searchParams, collegeId.toString(), null, null);
		
		return builder.build();
	}

	public static List<SolrCourse> getPreparedData1() {
		ArrayList<SolrCourse> courses = new ArrayList<>();
		courses.add(createSolrCourse(13L, "Test Cert 031", "test detail", "HTBFLM1"));
		courses.add(createSolrCourse(14L, "Test Cert 021", "test detail care", "HTBFLM2"));
		courses.add(createSolrCourse(15L, "Test Cert 011", "test detail aged", "HTBFLM3"));
		courses.add(createSolrCourse(16L, "Test Cert 001", "test detail", "HTBFLM4"));
		courses.add(createSolrCourse(17L, "Test Cert 002", "test detail", "HTBFLM5"));
		courses.add(createSolrCourse(18L, "Test Cert 003", "test detail aged care", "HTBFLM6"));
		courses.add(createSolrCourse(19L, "Test Cert 004", "test detail", "HTBFLM6"));
		return courses;
	}

	public static List<SolrCourse> getPreparedData2() {
		ArrayList<SolrCourse> courses = new ArrayList<>();
		courses.add(createSolrCourse(13L, "Work in aged care", "test detail", "HTBFLM1"));
		courses.add(createSolrCourse(14L, "Work in a facility for the aged where you care for residents.", "test detail", "HTBFLM2"));
		courses.add(createSolrCourse(15L, "Certificate III in Child Care", "test detail", "HTBFLM3"));
		courses.add(createSolrCourse(16L, "Test Cert 001", "Tablet devices are very popular with people over the age of 60. They are used to keep in touch with family and friends and show that you care about their life", "HTBFLM4"));
		courses.add(createSolrCourse(17L, "Test Cert 002", "Creating handmade cards for gifts show that you care to put in time and effort", "HTBFLM5"));
		courses.add(createSolrCourse(18L, "Test Cert 003", "This class is available for all ages", "HTBFLM6"));
		courses.add(createSolrCourse(19L, "Test Cert 004", "test detail", "HTBFLM7"));
		return courses;
	}
	
	public static SolrCourse createSolrCourse(Long id, String name, String detail, String code) {
		return createSolrCourse(id, collegeId, name, detail, code, startDate);
	}
	
	public static SolrCourse createSolrCourse(Long id, Long collegeId, String name, String detail, String code, Date startDate) {
		SolrCourse solrCourse = new SolrCourse();
		solrCourse.setId(id.toString());
		solrCourse.setCollegeId(collegeId);
		solrCourse.setName(name);
		solrCourse.setDetail(detail);
		solrCourse.setCode(code);
		solrCourse.setStartDate(startDate);
		return solrCourse;
	}
}
