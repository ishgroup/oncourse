package ish.oncourse.util;

import ish.oncourse.model.Course;
import ish.oncourse.model.Tag;
import org.apache.tapestry5.services.Request;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HTMLUtilsTest {

	@Test
	public void test_getUrlBy() {
		String expected = "http://localhost.localdomain/HTMLUtilsTest";
		String actual = HTMLUtils.getUrlBy("localhost.localdomain", HTMLUtilsTest.class);
		assertEquals(expected, actual);
	}

	@Test
	public void test_getUrlBy_Request() {

		String expected = "http://localhost.localdomain/context/HTMLUtilsTest";
		 Request request = mock(Request.class);
		when(request.getServerName()).thenReturn("localhost.localdomain");
		when(request.getContextPath()).thenReturn("context");
		String actual = HTMLUtils.getUrlBy(request, HTMLUtilsTest.class);
		assertEquals(expected, actual);
	}

	@Test
	public void test_parserBooleanValue() {

		assertTrue(HTMLUtils.parserBooleanValue("on"));
		assertTrue(HTMLUtils.parserBooleanValue("true"));
		assertFalse(HTMLUtils.parserBooleanValue("false"));
		assertFalse(HTMLUtils.parserBooleanValue("blablabla"));
		assertFalse(HTMLUtils.parserBooleanValue(null));
	}

	@Test
	public void test_getCanonicalLinkPathFor() {
		String expected = "http://localhost.localdomain/context/course/course1";
		Request request = createRequest();

		Course course = mock(Course.class);
		when(course.getCode()).thenReturn("course1");
		String actual = HTMLUtils.getCanonicalLinkPathFor(course, request);
		assertEquals(expected, actual);
	}

	private Request createRequest() {
		Request request = mock(Request.class);
		when(request.getServerName()).thenReturn("localhost.localdomain");
		when(request.getContextPath()).thenReturn("/context");
		return request;
	}

	@Test
	public void test_getCanonicalLinkPathForCourses() {
		String expected = "http://localhost.localdomain/context/courses/tag1.name/tag2.sname1+tag2.sname2";
		Tag tag1 = mock(Tag.class);
		when(tag1.getName()).thenReturn("tag1.name");
		when(tag1.getShortName()).thenReturn(null);
		when(tag1.getShortName()).thenReturn(null);

		Tag tag2 = mock(Tag.class);
		when(tag2.getName()).thenReturn("tag2.name");
		when(tag2.getShortName()).thenReturn("tag2.sname1 tag2.sname2");
		when(tag2.getDefaultPath()).thenCallRealMethod();
		when(tag2.getLink()).thenCallRealMethod();
		when(tag2.getLink(anyString())).thenCallRealMethod();
		when(tag2.getParent()).thenReturn(tag1);

		Request request = createRequest();
		when(request.getPath()).thenReturn("/tag1.name/tag2.name");
		String actual = HTMLUtils.getCanonicalLinkPathForCourses(request,tag2);
		assertEquals(expected, actual);

	}
}
