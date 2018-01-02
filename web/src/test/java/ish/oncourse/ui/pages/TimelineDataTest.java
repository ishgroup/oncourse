package ish.oncourse.ui.pages;

import ish.oncourse.test.context.CCourse;
import ish.oncourse.test.context.CCourseClass;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.dom.Element;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertTrue;

public class TimelineDataTest extends ATest {

	public static final String ROOM2 = "room2";
	public static final String ROOM1 = "room1";


	@Before
	public void before() {
		super.before();

		CCourse course = this.college.newCourse("Course1").withClass("C1", new Date());
		CCourseClass courseClass = course.getCourseClassBy("C1").withSession(new Date()).withSession(new Date());

		courseClass.getSessions().get(0).newRoom().getRoom().setName(ROOM1);
		courseClass.getSessions().get(1).newRoom().getRoom().setName(ROOM2);

		this.college.getObjectContext().commitChanges();
	}

	@Test
	public void testPageLoaded() {
		Document doc = tester.renderPage("LinkPageForTimelineData");
		Element link = doc.getElementById("link");
		doc = tester.clickLink(link);

		String responseResult = doc.toString();
		assertTrue(responseResult.contains(ROOM1));
		assertTrue(responseResult.contains(ROOM2));
		// encoded: "<dt>Map &amp; Address</dt><dd></dd>" - checks if there is
		// an
		// empty room
		assertTrue(responseResult
				.contains(
						"&amp;lt;dt&amp;gt;Map &amp;amp;amp; Address&amp;lt;/dt&amp;gt;"));
	}

}
