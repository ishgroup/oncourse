package ish.oncourse.ui.components;

import ish.oncourse.model.Tag;
import ish.oncourse.util.URLUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.services.Request;
import org.junit.Test;

import java.net.MalformedURLException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by akoiro on 28/04/2016.
 */
public class CourseSearchFormTest {


	@Test
	public void testBuildURL() throws MalformedURLException {
		String s = "word1 word2";
		Tag subject = mock(Tag.class);
		when(subject.getDefaultPath()).thenReturn("tag name1");
		String searchNear = "searchNear";
		String searchPrice = "100.0";
		String time = "time";
		String day = "day";

		String urlPath = String.format(CourseSearchForm.URL_PATH_SEARCH_PATTERN,
				(s == null ? StringUtils.EMPTY : s),
				(subject == null ? StringUtils.EMPTY : subject.getDefaultPath()),
				(searchNear == null ? StringUtils.EMPTY : searchNear),
				(searchPrice == null ? StringUtils.EMPTY : searchPrice),
				(time == null ? StringUtils.EMPTY : time),
				(day == null ? StringUtils.EMPTY : day));

		Request request = mock(Request.class);
		assertNotNull(URLUtils.buildURL(request, urlPath, false));
	}
}
