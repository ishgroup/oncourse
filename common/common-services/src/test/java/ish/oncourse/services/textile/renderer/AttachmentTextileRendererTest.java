package ish.oncourse.services.textile.renderer;

import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import ish.oncourse.model.BinaryData;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.textile.TextileUtil;
import ish.oncourse.services.textile.attrs.AttachmentTextileAttributes;
import ish.oncourse.util.IPageRenderer;
import ish.oncourse.util.ValidationErrors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AttachmentTextileRendererTest {
	
	private static final String TEST_ATTACHMENT_NAME = "testAttachment";
	private static final String SUCCESSFULLY_RENDERED = "success";
	
	@Mock
	private IBinaryDataService binaryDataService;
	
	@Mock
	private IPageRenderer pageRenderer;
	
	@Mock
	private BinaryInfo binaryInfo;
	
	private AttachmentTextileRenderer attachmentTextileRenderer;
	
	private ValidationErrors errors;
	
	@Before
	public void init() {
		errors = new ValidationErrors();
		attachmentTextileRenderer = new AttachmentTextileRenderer(binaryDataService, pageRenderer);
	}
	
	@Test
	public void testAttachmentRendering() {
		BinaryData binaryData = new BinaryData();
		when(binaryInfo.getBinaryData()).thenReturn(binaryData);
		when(binaryDataService.getBinaryInfo(BinaryInfo.NAME_PROPERTY, TEST_ATTACHMENT_NAME))
				.thenReturn(binaryInfo);
		
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> attachmentParams = new HashMap<String, Object>();
		attachmentParams.put(AttachmentTextileAttributes.ATTACHMENT_PARAM_NAME.getValue(), TEST_ATTACHMENT_NAME);
		params.put(TextileUtil.TEXTILE_ATTACHMENT_PAGE_PARAM, attachmentParams);
		when(pageRenderer.renderPage(TextileUtil.TEXTILE_ATTACHMENT_PAGE, params))
				.thenReturn(SUCCESSFULLY_RENDERED);
		
		String result = attachmentTextileRenderer.render(
				"{attachment name:\"" + TEST_ATTACHMENT_NAME + "\"}", errors);
		
		assertFalse(errors.hasFailures());
		assertEquals(SUCCESSFULLY_RENDERED, result);
	}
	
	@Test
	public void testAttachmentRenderingFailed() {
		String textile = "{attachment with invalid syntax}";
		String result = attachmentTextileRenderer.render(textile, errors);
		assertTrue(errors.hasFailures());
		assertEquals(textile, result);
	}

}
