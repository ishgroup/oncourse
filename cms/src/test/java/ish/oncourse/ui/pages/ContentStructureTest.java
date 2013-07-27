package ish.oncourse.ui.pages;

import ish.oncourse.cms.services.TestModuleForContentStructure;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.test.PageTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;


public class ContentStructureTest {

	private static final String appPackage = "ish.oncourse.ui";

	@Before
	public void setup() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testPageLoaded() {
		PageTester tester = new PageTester(appPackage, "", "src/main/webapp", TestModuleForContentStructure.class);
		Document doc = tester.renderPage("LinkPageForContentStructure");
		assertNotNull(doc);
		assertNotNull(doc.toString().contains("<img"));
		int numberStart = doc.toString().indexOf("<img");
		int numberEnd = doc.toString().indexOf("/>", numberStart) + 2;
		String imgElement = doc.toString().substring(numberStart, numberEnd);

		assertNotNull(imgElement.contains("binarydata?name="));

		tester.shutdown();
	}
}
