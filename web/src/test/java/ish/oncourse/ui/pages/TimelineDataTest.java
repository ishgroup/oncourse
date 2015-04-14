package ish.oncourse.ui.pages;

import ish.oncourse.ui.services.TestModule;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.test.PageTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TimelineDataTest {

	public static final String ROOM2 = "room2";
	public static final String ROOM1 = "room1";

	@Before
	public void setup() {

	}

	@After
	public void tearDown() {

	}

	@Test
	public void testPageLoaded() {
		PageTester tester = new PageTester("ish.oncourse.ui", "", "src/main/webapp", TestModule.class);
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
						"&amp;lt;dt&amp;gt;Map &amp;amp;amp; Address&amp;lt;/dt&amp;gt;&amp;lt;dd&amp;gt;&amp;lt;/dd&amp;gt;&amp;lt;dt&amp;gt;"));
		tester.shutdown();
	}

}
