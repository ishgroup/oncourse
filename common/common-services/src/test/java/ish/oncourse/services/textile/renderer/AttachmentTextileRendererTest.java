package ish.oncourse.services.textile.renderer;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.Document;
import ish.oncourse.model.DocumentVersion;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.AttachmentTextileAttributes;
import ish.oncourse.util.IPageRenderer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AttachmentTextileRendererTest {
	
	private static final String TEST_ATTACHMENT_NAME = "testAttachment";
	private static final String SUCCESSFULLY_RENDERED = "success";
	
	@Mock
	private IBinaryDataService binaryDataService;

	@Mock
	private IPageRenderer pageRenderer;
	
	@Mock
	private Document document;
	
	@Mock
	private DocumentVersion documentVersion;
	
	private AttachmentTextileRenderer renderer;
	
	@Before
	public void init() {
		renderer = new AttachmentTextileRenderer(binaryDataService, pageRenderer);
	}
	
	@Test
	public void testAttachmentRendering() {
		when(binaryDataService.getBinaryInfo(BinaryInfo.NAME_PROPERTY, TEST_ATTACHMENT_NAME))
				.thenReturn(document);

        Map<String, Object> params = new HashMap<>();
		Map<String, Object> attachmentParams = new HashMap<>();
		attachmentParams.put(AttachmentTextileAttributes.ATTACHMENT_PARAM_NAME.getValue(), TEST_ATTACHMENT_NAME);
		params.put(TextileUtil.TEXTILE_ATTACHMENT_PAGE_PARAM, attachmentParams);
		when(pageRenderer.renderPage(TextileUtil.TEXTILE_ATTACHMENT_PAGE, params))
				.thenReturn(SUCCESSFULLY_RENDERED);
		
		String result = renderer.render(
				"{attachment name:\"" + TEST_ATTACHMENT_NAME + "\"}");
		
		assertFalse(renderer.getErrors().hasFailures());
		assertEquals(SUCCESSFULLY_RENDERED, result);
	}
	
	@Test
	public void testAttachmentRenderingFailed() {
		String textile = "{attachment with invalid syntax}";
		String result = renderer.render(textile);
		assertTrue(renderer.getErrors().hasFailures());
		assertEquals("<span class=\"richtext_error\">Syntax error in \"{attachment with invalid syntax}\"</span><ol><li>The attachment tag '{attachment with invalid syntax}' doesn't match {attachment name:\"name\"}</li></ol>",
				result);
	}

}
