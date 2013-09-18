package ish.oncourse.services.textile.renderer;

import ish.oncourse.model.WebContent;
import ish.oncourse.services.content.IWebContentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BlockTextileRendererTest {
	
	private static final String BLOCK_CONTENT = "block content";

	@Mock
	private IWebContentService webContentService;

	private WebContent webContent;

	/**
	 * service under the test
	 */
	private BlockTextileRenderer renderer;

	@Before
	public void init() {
		webContent = new WebContent();
		webContent.setContent(BLOCK_CONTENT);
		renderer = new BlockTextileRenderer(webContentService, null);
	}

	/**
	 * Emulates the situation of {block} textile rendering
	 */
	@Test
	public void testRandomBlockRendering() {
		when(webContentService.getWebContent(null, null))
				.thenReturn(webContent);
		String result = renderer.render("{block}");
		assertFalse(renderer.getErrors().hasFailures());
		assertEquals(BLOCK_CONTENT, result);
	}

	/**
	 * Emulates the situation of {block} textile rendering, but there are no any
	 * notes in db
	 */
	@Test
	public void testBlockNotFound() {
		reset(webContentService);
		when(webContentService.getWebContent(null, null)).thenReturn(null);
		String result = renderer.render("{block}");
		assertFalse(renderer.getErrors().hasFailures());
		assertEquals("<span class=\"richtext_error\">Syntax error in \"{block}\"</span>", result);
	}
}
