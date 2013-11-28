package ish.oncourse.services.search;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

@RunWith(MockitoJUnitRunner.class)
public class SearchServiceTest {

	@Mock
	private ISearchService searchService;

	@Test
	public void testNulls() throws UnsupportedEncodingException, ParseException {
		SearchParams searchParams = new SearchParams();
		int start = 0;
		Integer rows = null;
		searchService.searchCourses(searchParams, start, rows);
	}
}
