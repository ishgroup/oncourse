package ish.oncourse.services.textile.renderer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.ValidationErrors;

@RunWith(MockitoJUnitRunner.class)
public class PageTextileRendererTest {
	
	private static final int TEST_PAGE_CODE = 100;
	
	private static final String SUCCESSFULLY_RENDERED = "success";
	
	private ValidationErrors errors;
	
	private WebNode page;

	/**
	 * service under the test
	 */
	private PageTextileRenderer pageTextileRenderer;
	
	@Mock
	private IWebNodeService webNodeService;
	
	@Mock
	private IPageRenderer pageRenderer;

	@Before
	public void init() {
		errors = new ValidationErrors();
		page = new WebNode();
		pageTextileRenderer = new PageTextileRenderer(webNodeService,
				pageRenderer);
		when(
				webNodeService.getNode(WebNode.NODE_NUMBER_PROPERTY,
						TEST_PAGE_CODE)).thenReturn(page);
		when(
				pageRenderer.renderPage(eq(TextileUtil.TEXTILE_PAGE_PAGE),
						anyMap())).thenReturn(SUCCESSFULLY_RENDERED);
	}

	/**
	 * Emulates rendering of page with given page number, ie {page code:"100"}
	 */
	@Test
	public void renderPageWithNodeNumberTest() {
		String tag = "{page code:\"" + TEST_PAGE_CODE + "\"}";
		String result = pageTextileRenderer.render(tag, errors);
		assertFalse(errors.hasFailures());
		assertEquals(SUCCESSFULLY_RENDERED, result);
	}
}
