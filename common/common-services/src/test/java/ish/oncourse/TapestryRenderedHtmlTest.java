package ish.oncourse;

import ish.oncourse.services.ServiceModule;
import ish.oncourse.test.ContextUtils;
import ish.oncourse.util.IPageRenderer;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.internal.test.TestableRequest;
import org.apache.tapestry5.internal.test.TestableResponse;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.test.PageTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TapestryRenderedHtmlTest {

	private PageTester tester;
	private IPageRenderer pageRenderer;
	
	@Before
	public void setup() throws Exception {
		ContextUtils.setupDataSources();
		
		tester = new PageTester("ish.oncourse.test", "", PageTester.DEFAULT_CONTEXT_PATH, ServiceModule.class);
		pageRenderer = tester.getRegistry().getService(IPageRenderer.class);
	}
	
	private void prepareRequestGlobals() {
		Request request = tester.getRegistry().getService(TestableRequest.class);
		Response response = tester.getRegistry().getService(TestableResponse.class);

		RequestGlobals requestGlobals = tester.getRegistry().getService(RequestGlobals.class);
		requestGlobals.storeRequestResponse(request, response);
	}
	
	@After
	public void tearDown() {

	}

	@Test
	public void testFunction() {
		Document doc = tester.renderPage("simpletestpage");
		assertNotNull(doc.getElementById("divBlock"));
		
		prepareRequestGlobals();
		
		String result = pageRenderer.renderPage("simpletestpage", new HashMap<String, Object>());
		assertTrue(result.contains("divBlock"));
	}
}
