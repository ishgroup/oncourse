package ish.oncourse.services.html;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class JerichoPlainTextExtractorTest {

	private static final String SOME_CONTENT = "Some content";
	private static JerichoPlainTextExtractor extractor;

	@BeforeClass
	public static void init() {
		extractor = new JerichoPlainTextExtractor();
	}

	@Test
	public void compactHtmlTagExceptionsTest() {

		StringBuffer resultBuffer = new StringBuffer();
		assertNull(extractor.compactHtmlTag(null, resultBuffer, 0));

		assertTrue(resultBuffer.length() == 0);
		assertNull(extractor.compactHtmlTag(SOME_CONTENT, null, 0));
		assertNull(extractor.compactHtmlTag(SOME_CONTENT, resultBuffer, SOME_CONTENT.length() + 40));
		assertTrue(resultBuffer.length() == 0);
	}

	@Test
	public void compactHtmlTagTest() {
		StringBuffer resultBuffer = new StringBuffer();
		assertEquals(extractor.compactHtmlTag(SOME_CONTENT, resultBuffer, 0), (Integer) SOME_CONTENT.length());
		assertEquals(resultBuffer.toString(), SOME_CONTENT);
		resultBuffer = new StringBuffer();
		assertEquals(extractor.compactHtmlTag(SOME_CONTENT, resultBuffer, 5), (Integer) SOME_CONTENT.length());
		assertEquals(resultBuffer.toString(), SOME_CONTENT.substring(5));
		resultBuffer = new StringBuffer();
		extractor.compactHtmlTag("", resultBuffer, 0);
		assertEquals(resultBuffer.toString(), "");
		resultBuffer = new StringBuffer();
		extractor.compactHtmlTag("<", resultBuffer, 0);
		assertEquals(resultBuffer.toString(), "<");
		resultBuffer = new StringBuffer();
		extractor.compactHtmlTag(">", resultBuffer, 0);
		assertEquals(resultBuffer.toString(), ">");
		resultBuffer = new StringBuffer();
		extractor.compactHtmlTag("<>", resultBuffer, 0);
		assertEquals(resultBuffer.toString(), "<>");
		resultBuffer = new StringBuffer();
		extractor.compactHtmlTag("><", resultBuffer, 0);
		assertEquals(resultBuffer.toString(), "><");
		resultBuffer = new StringBuffer();
		extractor.compactHtmlTag("< />", resultBuffer, 0);
		assertEquals(resultBuffer.toString(), "< />");
		resultBuffer = new StringBuffer();
		assertEquals(extractor.compactHtmlTag("<span> \n bla-bla-bla", resultBuffer, 0), (Integer) "<span>".length());
		assertEquals(resultBuffer.toString(), "<span />");
		resultBuffer = new StringBuffer();
		extractor.compactHtmlTag("<span> \n bla-bla-bla</span>", resultBuffer, 0);
		assertEquals(resultBuffer.toString(), "<span> bla-bla-bla</span>");
		resultBuffer = new StringBuffer();
		extractor.compactHtmlTag("<some \n tag='f'>", resultBuffer, 0);
		assertEquals(resultBuffer.toString(), "<some tag=\"f\" />");
		resultBuffer = new StringBuffer();
		extractor.compactHtmlTag("<some \n tag='f'/>", resultBuffer, 0);
		assertEquals(resultBuffer.toString(), "<some tag=\"f\" />");
	}

	@Test
	public void compactHtmlTagsTest() {
		assertEquals(extractor.compactHtmlTags(null), "");
		assertEquals(extractor.compactHtmlTags(""), "");
		assertEquals(extractor.compactHtmlTags("<><><>>>>>plain \n text <html>\n\n\n</html>"), "<><><>>>>>plain \n"
				+ " text <html></html>");

		InputStream st = JerichoPlainTextExtractorTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/services/html/htmlData.txt");
		String data = "";
		try {
			data = IOUtils.toString(st, "UTF-8");
		} catch (IOException e) {
		}
		assertEquals(
				extractor.compactHtmlTags(data),
				"<h1>Short Courses at Sydney University. Open to Everyone.</h1>"
						+ "\n\t\t<p>We're one of Australia's leading providers of <strong>short courses</strong> , <strong>professional development courses</strong> and <strong>corporate training courses</strong> , meeting the learning &amp; education needs of the <strong>Sydney community</strong> at all stages of life and across a diverse range of interests. We're like a <strong>community college</strong> , but with access to the expertise of the <strong>University of Sydney</strong> . Gain new insights, learn new skills or discover your untapped creativity - <strong>over 700 courses</strong> make it all possible at <strong>CCE - the Centre for Continuing Education at the University of Sydney</strong> . </p>");

	}
}
