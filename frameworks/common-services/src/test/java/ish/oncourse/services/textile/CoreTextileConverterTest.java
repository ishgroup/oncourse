package ish.oncourse.services.textile;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class CoreTextileConverterTest {

	private ITextileConverter textileConverter;

	@Before
	public void init() {
		textileConverter = new TextileConverter();
	}

	@Test
	public void testConvertCoreTextile() {
		assertEquals(
				"<p><em>emphasized</em> <strong>strongly emphasized</strong></p>",
				textileConverter
						.convertCoreTextile("_emphasized_ *strongly emphasized*"));

		assertEquals(
				"<ul><li>An item in a bulleted (unordered) list<ul><li>Second Level</li></ul></li></ul>",
				textileConverter
						.convertCoreTextile("* An item in a bulleted (unordered) list\n** Second Level"));

		assertEquals(
				"<ol><li>An item in an enumerated (ordered) list xxxxxxx"
						+ "<ol><li>Another level in an enumerated list vvvvvvvv</li>"
						+ "<li>this is a numbered list</li></ol></li></ol>",
				textileConverter
						.convertCoreTextile("# An item in an enumerated (ordered) list xxxxxxx\n"
								+ "## Another level in an enumerated list vvvvvvvv"
								+ "\n*# this is a numbered list"));

		assertEquals(
				"<table><tr><th>Header </th><th>Header </th><th>Header </th></tr>"
						+ "<tr><td>Cell 1 </td><td>Cell 2 </td><td>Cell 3 </td></tr></table>",
				textileConverter
						.convertCoreTextile("|_. Header |_. Header |_. Header |\n| Cell 1 | Cell 2 | Cell 3 |"));

		assertEquals("<p><code>code</code></p>", textileConverter
				.convertCoreTextile("@code@"));

		assertEquals(
				"<h1 id=\"id\">An HTML first-level heading</h1>",
				textileConverter
						.convertCoreTextile("h1(#id). An HTML first-level heading"));

		assertEquals(
				"<blockquote><p>This text will be enclosed in an HTML blockquote element.</p></blockquote>",
				textileConverter
						.convertCoreTextile("bq. This text will be enclosed in an HTML blockquote element."));

		assertEquals("<p><a href=\"link_address\">link text</a></p>",
				textileConverter
						.convertCoreTextile("\"link text\":link_address"));

		assertEquals(
				"<p><img alt=\"(alt text)\" title=\"(alt text)\" border=\"0\" src=\"imageurl\"/></p>",
				textileConverter.convertCoreTextile("!imageurl(alt text)!"));

	}
}
