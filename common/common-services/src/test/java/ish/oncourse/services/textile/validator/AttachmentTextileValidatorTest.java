package ish.oncourse.services.textile.validator;

import ish.oncourse.model.Document;
import ish.oncourse.model.DocumentVersion;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.textile.attrs.AttachmentTextileAttributes;
import ish.oncourse.util.ValidationErrors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AttachmentTextileValidatorTest extends CommonValidatorTest {
	
	private static final String TEST_ATTACHMENT_NAME = "testAttachment";
	private static final String NOT_EXISTING_ATTACHMENT_NAME = "noAttachment";
	private static final String EMPTY_BINARY_INFO_ATTACHMENT_NAME = "emptyAttachment";
	
	@Mock
	private IBinaryDataService binaryDataService;

	@Mock
	private Document document;
	
	@Mock
	private DocumentVersion documentVersion;
	
	@Mock
	private Document emptyDocument
			;
	

	@Override
	public void init() {
		validator = new AttachmentTextileValidator(binaryDataService);
		errors = new ValidationErrors();

		when(binaryDataService.getBinaryInfo(Document.NAME_PROPERTY, TEST_ATTACHMENT_NAME)).thenReturn(document);

        when(binaryDataService.getBinaryInfo(Document.NAME_PROPERTY, NOT_EXISTING_ATTACHMENT_NAME)).thenReturn(null);
		when(binaryDataService.getBinaryInfo(Document.NAME_PROPERTY, EMPTY_BINARY_INFO_ATTACHMENT_NAME)).thenReturn(emptyDocument);
	}

	@Override
	protected String getTextileForSmokeTest() {
		return "{attachment name:\"" + TEST_ATTACHMENT_NAME + "\"}";
	}

	@Override
	protected Map<String, String> getDataForUniquenceTest() {
		Map<String, String> data = new HashMap<>();
		for (AttachmentTextileAttributes attr : AttachmentTextileAttributes.values()) {
			switch (attr) {
			case ATTACHMENT_PARAM_NAME:
				data.put(AttachmentTextileAttributes.ATTACHMENT_PARAM_NAME.getValue(), 
						"{attachment name:\"" + TEST_ATTACHMENT_NAME + "\" name:\"anotherName\"}");
				break;
			}
		}
		return data;
	}

	@Override
	protected String getIncorrectFormatTextile() {
		return "{attachment name=\"someName\"";
	}
	
	@Test
	public void testBinaryInfoNotFound() {
		String tag = "{attachment name:\"" + NOT_EXISTING_ATTACHMENT_NAME + "\"}";
		validator.validate(tag, errors);
		assertTrue(errors.hasFailures());
		assertTrue(errors.contains(((AttachmentTextileValidator) validator)
				.getNotFoundMessage(NOT_EXISTING_ATTACHMENT_NAME)));
	}

	@Test
	public void testBinaryDataNotFound() {
		String tag = "{attachment name:\"" + EMPTY_BINARY_INFO_ATTACHMENT_NAME + "\"}";
		validator.validate(tag, errors);
		assertFalse(errors.hasFailures());
	}

}
