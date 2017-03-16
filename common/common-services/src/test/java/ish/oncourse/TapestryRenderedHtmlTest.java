package ish.oncourse;

import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.test.PageTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class TapestryRenderedHtmlTest {

	private PageTester tester;
	
	@Before
	public void setup() throws Exception {
		tester = new PageTester("ish.oncourse.test", "");
	}
	
	@After
	public void tearDown() {

	}

	@Test
	public void testPageLoad() {
		Document doc = tester.renderPage("simpletestpage");
		assertNotNull(doc.getElementById("divBlock"));
	}
}
