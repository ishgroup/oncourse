package ish.oncourse.services.textile.validator;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import ish.oncourse.model.BinaryData;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.services.binary.IBinaryDataService;
import ish.oncourse.services.textile.attrs.AttachmentTextileAttributes;
import ish.oncourse.util.ValidationErrors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AttachmentTextileValidatorTest extends CommonValidatorTest {
	
	private static final String TEST_ATTACHMENT_NAME = "testAttachment";
	private static final String NOT_EXISTING_ATTACHMENT_NAME = "noAttachment";
	private static final String EMPTY_BINARY_INFO_ATTACHMENT_NAME = "emptyAttachment";
	
	@Mock
	private IBinaryDataService binaryDataService;
	
	@Mock
	private BinaryInfo binaryInfo;
	
	@Mock
	private BinaryInfo emptyBinaryInfo;
	
	private BinaryData binaryData;

	@Override
	public void init() {
		validator = new AttachmentTextileValidator(binaryDataService);
		errors = new ValidationErrors();
		binaryData = new BinaryData();
		
		when(binaryDataService.getBinaryInfo(BinaryInfo.NAME_PROPERTY, TEST_ATTACHMENT_NAME)).thenReturn(binaryInfo);
		when(binaryInfo.getBinaryData()).thenReturn(binaryData);
		
		when(binaryDataService.getBinaryInfo(BinaryInfo.NAME_PROPERTY, NOT_EXISTING_ATTACHMENT_NAME)).thenReturn(null);
		when(binaryDataService.getBinaryInfo(BinaryInfo.NAME_PROPERTY, EMPTY_BINARY_INFO_ATTACHMENT_NAME)).thenReturn(emptyBinaryInfo);
		
		when(emptyBinaryInfo.getBinaryData()).thenReturn(null);
	}

	@Override
	protected String getTextileForSmokeTest() {
		return "{attachment name:\"" + TEST_ATTACHMENT_NAME + "\"}";
	}

	@Override
	protected Map<String, String> getDataForUniquenceTest() {
		Map<String, String> data = new HashMap<String, String>();
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
		assertTrue(errors.hasFailures());
		assertTrue(errors.contains(((AttachmentTextileValidator) validator).getNotFoundContentMessage()));
	}

}
