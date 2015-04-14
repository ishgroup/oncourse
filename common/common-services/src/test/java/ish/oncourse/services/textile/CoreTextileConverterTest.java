package ish.oncourse.services.textile;

import ish.oncourse.services.html.IPlainTextExtractor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CoreTextileConverterTest {

	private static final String TEST9 = "!imageurl(alt text)!";

	private static final String TEST8 = "\"link text\":link_address";

	private static final String TEST7 = "bq. This text will be enclosed in an HTML blockquote element.";

	private static final String TEST6 = "h1(#id). An HTML first-level heading";

	private static final String TEST5 = "@code@";

	private static final String TEST4 = "|_. Header |_. Header |_. Header |\n| Cell 1 | Cell 2 | Cell 3 |";

	private static final String TEST3 = "# An item in an enumerated (ordered) list xxxxxxx\n"
			+ "## Another level in an enumerated list vvvvvvvv" + "\n*# this is a numbered list";

	private static final String TEST2 = "* An item in a bulleted (unordered) list\n** Second Level";

	private static final String TEST1 = "_emphasized_ *strongly emphasized*";

	private ITextileConverter textileConverter;

	@Mock
	private IPlainTextExtractor extractor;

	@Before
	public void init() {
		when(extractor.compactHtmlTags(TEST1)).thenReturn(TEST1);
		when(extractor.compactHtmlTags(TEST2)).thenReturn(TEST2);
		when(extractor.compactHtmlTags(TEST3)).thenReturn(TEST3);
		when(extractor.compactHtmlTags(TEST4)).thenReturn(TEST4);
		when(extractor.compactHtmlTags(TEST5)).thenReturn(TEST5);
		when(extractor.compactHtmlTags(TEST6)).thenReturn(TEST6);
		when(extractor.compactHtmlTags(TEST7)).thenReturn(TEST7);
		when(extractor.compactHtmlTags(TEST8)).thenReturn(TEST8);
		when(extractor.compactHtmlTags(TEST9)).thenReturn(TEST9);
		textileConverter = new TextileConverter();
	}

	@Test
	public void testConvertCoreTextile() {

		assertEquals("<em>emphasized</em> <strong>strongly emphasized</strong>",
				textileConverter.convertCoreTextile(TEST1));

		assertEquals("<ul><li>An item in a bulleted (unordered) list<ul><li>Second Level</li></ul></li></ul>",
				textileConverter.convertCoreTextile(TEST2));

		assertEquals("<ol><li>An item in an enumerated (ordered) list xxxxxxx"
				+ "<ol><li>Another level in an enumerated list vvvvvvvv</li>"
				+ "<li>this is a numbered list</li></ol></li></ol>", textileConverter.convertCoreTextile(TEST3));

		assertEquals("<table><tr><th>Header </th><th>Header </th><th>Header </th></tr>"
				+ "<tr><td>Cell 1 </td><td>Cell 2 </td><td>Cell 3 </td></tr></table>",
				textileConverter.convertCoreTextile(TEST4));

		assertEquals("<code>code</code>", textileConverter.convertCoreTextile(TEST5));

		assertEquals("<h1 id=\"id\">An HTML first-level heading</h1>", textileConverter.convertCoreTextile(TEST6));

		assertEquals("<blockquote><p>This text will be enclosed in an HTML blockquote element.</p></blockquote>",
				textileConverter.convertCoreTextile(TEST7));

		assertEquals("<a href=\"link_address\">link text</a>", textileConverter.convertCoreTextile(TEST8));

		assertEquals("<img alt=\"(alt text)\" title=\"(alt text)\" border=\"0\" src=\"imageurl\"/>",
				textileConverter.convertCoreTextile(TEST9));

	}
}
