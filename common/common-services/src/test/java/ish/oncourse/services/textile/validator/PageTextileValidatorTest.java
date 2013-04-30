package ish.oncourse.services.textile.validator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import ish.oncourse.model.WebNode;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.textile.attrs.PageTextileAttributes;
import ish.oncourse.util.ValidationErrors;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PageTextileValidatorTest extends CommonValidatorTest {
	private static final int TEST_PAGE_CODE = 100;
	private static final int NOT_EXISTING_PAGE_CODE = 200;
	@Mock
	private IWebNodeService webNodeService;

	private WebNode page;

	@Override
	protected Map<String, String> getDataForUniquenceTest() {
		Map<String, String> data = new HashMap<>();
		for (PageTextileAttributes attr : PageTextileAttributes.values()) {
			switch (attr) {
			case PAGE_CODE_PARAM:
				data.put(PageTextileAttributes.PAGE_CODE_PARAM.getValue(), "{page code:\"1111\" code:\"2222\"}");
				break;
			}
		}
		return data;
	}

	@Override
	protected String getIncorrectFormatTextile() {
		return "{page id:\"1111\"}";
	}

	@Override
	protected String getTextileForSmokeTest() {
		return "{page}";
	}

	@Override
	public void init() {
		errors = new ValidationErrors();
		page = new WebNode();
		validator = new PageTextileValidator(webNodeService);
		when(webNodeService.getNodeForNodeNumber(eq(TEST_PAGE_CODE))).thenReturn(page);
		when(webNodeService.getNodeForNodeNumber(eq(NOT_EXISTING_PAGE_CODE))).thenReturn(null);
	}

	/**
	 * Emulates the situations when there's page with given code and when
	 * there's no page with such a code
	 */
	@Test
	public void testCodeAttribute() {
		String tag = "{page code:\"" + TEST_PAGE_CODE + "\"}";
		validator.validate(tag, errors);
		assertFalse(errors.hasFailures());

		tag = "{page code:\"" + NOT_EXISTING_PAGE_CODE + "\"}";
		validator.validate(tag, errors);
		assertTrue(errors.hasFailures());
		assertTrue(errors.toString().contains(((PageTextileValidator) validator).getPageNotFoundByCode(NOT_EXISTING_PAGE_CODE)));
	}

	/**
	 * Emulates the situation when there is a new line in {page}, shouldn't be any errors.
	 */
	@Test
	public void pageWithNewLineTest() {
		String tag = "{page \n code:\"" + TEST_PAGE_CODE + "\"}";
		validator.validate(tag, errors);
		assertFalse(errors.hasFailures());
	}
}
