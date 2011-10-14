package ish.oncourse.services.textile.renderer;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import ish.oncourse.model.WebContent;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.util.ValidationErrors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BlockTextileRendererTest {
	
	private static final String BLOCK_CONTENT = "block content";

	@Mock
	private IWebContentService webContentService;

	private WebContent webContent;

	/**
	 * service under the test
	 */
	private BlockTextileRenderer blockTextileRenderer;

	private ValidationErrors errors;

	@Before
	public void init() {
		webContent = new WebContent();
		webContent.setContent(BLOCK_CONTENT);
		errors = new ValidationErrors();
		blockTextileRenderer = new BlockTextileRenderer(webContentService, null);
	}

	/**
	 * Emulates the situation of {block} textile rendering
	 */
	@Test
	public void testRandomBlockRendering() {
		when(webContentService.getWebContent(null, null))
				.thenReturn(webContent);
		String result = blockTextileRenderer.render("{block}", errors);
		assertFalse(errors.hasFailures());
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
		String result = blockTextileRenderer.render("{block}", errors);
		assertFalse(errors.hasFailures());
		assertNull(result);
	}
}
