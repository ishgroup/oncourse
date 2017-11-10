package ish.oncourse.solr.ordering;

import ish.oncourse.services.search.SearchParams;
import ish.oncourse.services.search.SolrQueryBuilder;
import ish.oncourse.solr.InitSolr;
import ish.oncourse.solr.model.SCourse;
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
		
		List<SCourse> preparedData = getPreparedData1();
		for (SCourse solrCourse : preparedData) {
			solrClient.addBean(solrCourse);
		}
		
		solrClient.commit();

		List<SCourse> allCourses = solrClient.query("courses", new SolrQuery("*:*")).getBeans(SCourse.class);
		assertEquals(7, allCourses.size());

		List<SCourse> courses = solrClient.query("courses", getSolrQuery()).getBeans(SCourse.class);
		
		assertEquals(3, courses.size());
		assertEquals("18", courses.get(0).getId());
		assertEquals("15", courses.get(1).getId());
		assertEquals("14", courses.get(2).getId());
	}

	@Test
	public void test2() throws IOException, SolrServerException {
		SolrClient solrClient = new EmbeddedSolrServer(h.getCore());

		List<SCourse> preparedData = getPreparedData2();
		for (SCourse solrCourse : preparedData) {
			solrClient.addBean(solrCourse);
		}

		solrClient.commit();

		List<SCourse> allCourses = solrClient.query("courses", new SolrQuery("*:*")).getBeans(SCourse.class);
		assertEquals(7, allCourses.size());

		List<SCourse> courses = solrClient.query("courses", getSolrQuery()).getBeans(SCourse.class);
				
		assertEquals(6, courses.size());
		assertEquals("13", courses.get(0).getId());
		assertEquals("14", courses.get(1).getId());
		assertEquals("15", courses.get(2).getId());
		assertEquals("16", courses.get(3).getId());
		assertEquals("18", courses.get(4).getId());
		assertEquals("17", courses.get(5).getId());
	}

	@Test
	public void test3() throws IOException, SolrServerException {
		SolrClient solrClient = new EmbeddedSolrServer(h.getCore());

		List<SCourse> preparedData = getPreparedData3();
		for (SCourse solrCourse : preparedData) {
			solrClient.addBean(solrCourse);
		}

		solrClient.commit();

		List<SCourse> allCourses = solrClient.query("courses", new SolrQuery("*:*")).getBeans(SCourse.class);
		assertEquals(7, allCourses.size());

		List<SCourse> courses = solrClient.query("courses", getSolrQuery()).getBeans(SCourse.class);

		assertEquals(6, courses.size());
		assertEquals("17", courses.get(0).getId());
		assertEquals("18", courses.get(1).getId());
		assertEquals("15", courses.get(2).getId());
		assertEquals("13", courses.get(3).getId());
		assertEquals("14", courses.get(4).getId());
		assertEquals("16", courses.get(5).getId());
	}
	
	private static SolrQuery getSolrQuery() {
		SearchParams searchParams = new SearchParams();
		searchParams.setS("aged care");
		searchParams = SearchParams.valueOf(searchParams, true);
		
		SolrQueryBuilder builder = SolrQueryBuilder.valueOf(searchParams, collegeId.toString(), null, null);
		
		return builder.build();
	}

	public static List<SCourse> getPreparedData1() {
		ArrayList<SCourse> courses = new ArrayList<>();
		courses.add(createSolrCourse(13L, "Test Cert 031", "test detail", "HTBFLM1"));
		courses.add(createSolrCourse(14L, "Test Cert 021", "test detail care", "HTBFLM2"));
		courses.add(createSolrCourse(15L, "Test Cert 011", "test detail aged", "HTBFLM3"));
		courses.add(createSolrCourse(16L, "Test Cert 001", "test detail", "HTBFLM4"));
		courses.add(createSolrCourse(17L, "Test Cert 002", "test detail", "HTBFLM5"));
		courses.add(createSolrCourse(18L, "Test Cert 003", "test detail aged care", "HTBFLM6"));
		courses.add(createSolrCourse(19L, "Test Cert 004", "test detail", "HTBFLM6"));
		return courses;
	}

	public static List<SCourse> getPreparedData2() {
		ArrayList<SCourse> courses = new ArrayList<>();
		courses.add(createSolrCourse(13L, "Work in aged care", "test detail", "HTBFLM1"));
		courses.add(createSolrCourse(14L, "Work in a facility for the aged where you care for residents.", "test detail", "HTBFLM2"));
		courses.add(createSolrCourse(15L, "Certificate III in Child Care", "test detail", "HTBFLM3"));
		courses.add(createSolrCourse(16L, "Test Cert 001", "Tablet devices are very popular with people over the age of 60. They are used to keep in touch with family and friends and show that you care about their life", "HTBFLM4"));
		courses.add(createSolrCourse(17L, "Test Cert 002", "Creating handmade cards for gifts show that you care to put in time and effort", "HTBFLM5"));
		courses.add(createSolrCourse(18L, "Test Cert 003", "This class is available for all ages", "HTBFLM6"));
		courses.add(createSolrCourse(19L, "Test Cert 004", "test detail", "HTBFLM7"));
		return courses;
	}
	
	public static List<SCourse> getPreparedData3() {
		ArrayList<SCourse> courses = new ArrayList<>();
		courses.add(createSolrCourse(13L, "Diploma of Early Childhood Education and Care", "The CHC50113 Diploma of Early Childhood Education and Care is the national qualification for child care workers who want to train to be a Team Coordinator or Leader, a Child Development Worker or Children’s Services Director.\n" +
				"\nAbout\nThis qualification reflects the role of early childhood educators who are responsible for designing and implementing curriculum in early childhood education and care services. In doing so, they work to implement an approved learning framework within the requirements of the Education and Care Services National Regulations and the National Quality Standard. They may have responsibility for supervision of volunteers or other staff. This is an ideal course for people wishing to advance their career in the Children’s Services sector."
				, "DiplomaEarlyChildcare2017"));
		courses.add(createSolrCourse(14L, "Certificate III in Early Childhood Education and Care", "Looking for a fun and rewarding career? Use this Nationally Recognised Qualification to enter work in the child care sector such as day-care centres, playgroups and family day-care. You will learn how to interact with children and to respond effectively to their physical, social, emotional and developmental needs. Occupational health and safety, nutrition, and legal considerations are also covered. Students will be required to complete 120 hours work experience in a child care facility to provide useful ‘real world’ experience in addition to the course hours plus at least 6 hours self-paced study per week.\n" +
				"\nAbout\n\n" +
				"CHC30113 Certificate III in Early Childhood Education and Care is the national qualification for child care workers and will prepare you to work in early childhood with the knowledge and skills to support child development. This course covers interesting aspects of child development, play support, behaviour management and health and safety. Students will complete work experience in a child care facility as part of completing this course.", "Cert3Childcare1"));
		courses.add(createSolrCourse(15L, "Introduction to Early Childhood Education and Care", "Looking for a fun and rewarding career that is also flexible to suit your lifestyle? Use this short course as the first stepping stone towards your career in child care. Gain valuable real life work skills within the early childhood sector and work towards your next job at a day-care centre, playgroup or your own family day-care. Held in Mullumbimby the course covers 9 units from the CHC30113 Certificate III in Early Childhood Education and Care.", "IntrotoECEC"));
		courses.add(createSolrCourse(16L, "Provide An Emergency First Aid Response In An Education & Care Setting", "This course is for educators and support staff working within an education and care setting who are required to respond to a first aid emergency, including asthmatic and anaphylactic emergencies. It is a one-day course based on pre-reading and completion of a workbook before attendance at the course. The workbook will be available when you enrol. Course fee includes a manual and face shield. Refresher courses are now done within this one-day format and, although the pre-reading and completion of the workbook are not required for a refresher, they are recommended. This course is conducted by Parasol EMT (RTO number 2551) who will issue successful participants with a Statement of Attainment, this is valid for three years & the CPR unit must be renewed each year.", "EmergencyFirstAid"));
		courses.add(createSolrCourse(17L, "Certificate III In Individual Support (Ageing)NRT", "Aged Care\n" +
				"\n" +
				"The CHC33015 Certificate III in Individual Support (Ageing) is the minimum qualification required to work in the Aged Care industry. This course provides skills and knowledge to work as a Care Assistant, Nursing Assistant, Home Care Assistant in an Aged Care Facility or within the Community.\n" +
				"\n" +
				"About\n" +
				"\n" +
				"A career in Aged Care can be challenging and rewarding. People suited to this profession often demonstrate genuine compassion and empathy towards others. The Aged Care Industry is a growing sector, providing graduates with flexibility and career options nationwide.", "Cert3AgedCareAgeing"));
		courses.add(createSolrCourse(18L, "Certificate III In Individual Support (Disability)NRT", "The CHC33015 Certificate III in Individual Support (Disability) is the minimum qualification required to work in the Disability industry. This course provides skills and knowledge to work as a disability support worker in an aged care Facility or within the Community working with people with a disability.\n" +
				"\n" +
				"About\n" +
				"\n" +
				"A career in Disability can be challenging and rewarding. People suited to this profession often demonstrate genuine compassion and empathy towards others. The Disability Industry is a growing sector, providing graduates with flexibility and career options nationwide.", "Cert3AgedCareDisability"));
		courses.add(createSolrCourse(19L, "Test Cert 004", "test detail", "HTBFLM7"));
		return courses;
	}
	
	public static SCourse createSolrCourse(Long id, String name, String detail, String code) {
		return createSolrCourse(id, collegeId, name, detail, code, startDate);
	}
	
	public static SCourse createSolrCourse(Long id, Long collegeId, String name, String detail, String code, Date startDate) {
		SCourse solrCourse = new SCourse();
		solrCourse.setId(id.toString());
		solrCourse.setCollegeId(collegeId);
		solrCourse.setName(name);
		solrCourse.setDetail(detail);
		solrCourse.setCode(code);
		solrCourse.setStartDate(startDate);
		return solrCourse;
	}
}
