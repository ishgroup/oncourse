package ish.oncourse.ui.components;

import ish.oncourse.model.SearchParam;
import org.apache.tapestry5.services.Request;
import org.junit.Test;

import java.net.URL;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * Created by akoiro on 28/04/2016.
 */
public class SearchInputsTest {
	@Test
	public void testBuildURL() throws Exception {
		Request request = mock(Request.class);
		URL url = SearchInputs.BuildURL.valueOf(request, "/Tag1 1", Collections.singletonList("/Tag 2/Tag2 1"), Collections.singletonMap(SearchParam.s, "word1 word2")).build();
		assertNotNull(url);
		assertEquals("/courses/Tag1+1", url.getPath());
		assertEquals("s=word1+word2&tag=/Tag+2/Tag2+1", url.getQuery());
	}
}
