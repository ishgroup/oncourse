package ish.oncourse.services.markdown

import ish.oncourse.services.IRichtextConverter
import ish.oncourse.services.RichtextConverter
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals

class CoreMarkdownConverterTest {

    private static final String MD_MARKER = ' {render:"md"}'

    private static final String TEST_ITALICS = "_italic text first variant_; *italic text second variant*"

    private static final String TEST_BOLD_ITALIC = "__bold text *first* variant__"

    private static final String TEST_HEADINGS = "# HEADING1LEVEL\nsome text\n### HEADING3LEVEL\nsome text"

    private static final String TEST_BLOCKQOUTE = "> First line\n>\n> Second line\n>\n> Third line"

    private static final String TEST_UNORDERED_LIST = "+ Flour\n+ Cheese\n+ Tomatoes"

    private static final String TEST_ORDERED_LIST = "List:\n1. First\n2. Second\n3. Third"

    private static final String TEST_LINK_FIRST = "You can do anything at <https://google.com>"

    private static final String TEST_LINK_SECOND = "You can do anything at [our site](http://www.ur.ac.rw)."

    private static final String TEST_IMAGE = "![](https://commonmark.org/help/images/favicon.png)"

    private static final String TEST_CODE_SIMPLE = "When `x = 3`, that means `x + 2 = 5`"

    private static final String TEST_CODE_BLOCK = "There are salaries of employees?\n```\nTom 1000\nSam 1100\nRobin 600\n```"

    private static final String FULL_TEST = "## Course description\n__Attention, only for under 18!__\n\nYou need next:\n1. Pen\n2. Paper\n3. Document\n\nPrice is `\$150`\n\nAll extra information you can find [here](https://www.google.com/)\n"

    private IRichtextConverter markdownConverter

    @Before
    void init() {
        this.markdownConverter = new RichtextConverter()
    }

    @Test
    void testConvert() {
        assertEquals("<em>italic text first variant</em>; <em>italic text second variant</em>",
                markdownConverter.convertCoreText(TEST_ITALICS + MD_MARKER))

        assertEquals("<strong>bold text <em>first</em> variant</strong>",
                markdownConverter.convertCoreText(TEST_BOLD_ITALIC + MD_MARKER))

        assertEquals("<h1>HEADING1LEVEL</h1>\n<p>some text</p>\n<h3>HEADING3LEVEL</h3>\n<p>some text</p>",
                markdownConverter.convertCoreText(TEST_HEADINGS + MD_MARKER))

        assertEquals("<blockquote>\n<p>First line</p>\n<p>Second line</p>\n<p>Third line</p>\n</blockquote>",
                markdownConverter.convertCoreText(TEST_BLOCKQOUTE + MD_MARKER))

        assertEquals("<ul>\n<li>Flour</li>\n<li>Cheese</li>\n<li>Tomatoes</li>\n</ul>",
                markdownConverter.convertCoreText(TEST_UNORDERED_LIST + MD_MARKER))

        assertEquals("<p>List:</p>\n<ol>\n<li>First</li>\n<li>Second</li>\n<li>Third</li>\n</ol>",
                markdownConverter.convertCoreText(TEST_ORDERED_LIST + MD_MARKER))

        assertEquals("You can do anything at <a href=\"https://google.com\">https://google.com</a>",
                markdownConverter.convertCoreText(TEST_LINK_FIRST + MD_MARKER))

        assertEquals("You can do anything at <a href=\"http://www.ur.ac.rw\">our site</a>.",
                markdownConverter.convertCoreText(TEST_LINK_SECOND + MD_MARKER))

        assertEquals("<img src=\"https://commonmark.org/help/images/favicon.png\" alt=\"\" />",
                markdownConverter.convertCoreText(TEST_IMAGE + MD_MARKER))

        assertEquals("When <code>x = 3</code>, that means <code>x + 2 = 5</code>",
                markdownConverter.convertCoreText(TEST_CODE_SIMPLE + MD_MARKER))

        assertEquals("<p>There are salaries of employees?</p>\n<pre><code>Tom 1000\nSam 1100\nRobin 600\n</code></pre>",
                markdownConverter.convertCoreText(TEST_CODE_BLOCK + MD_MARKER))

        assertEquals("<h2>Course description</h2>\n<p><strong>Attention, only for under 18!</strong></p>\n<p>You need next:</p>\n<ol>\n<li>Pen</li>\n<li>Paper</li>\n<li>Document</li>\n</ol>\n<p>Price is <code>\$150</code></p>\n<p>All extra information you can find <a href=\"https://www.google.com/\">here</a></p>",
                markdownConverter.convertCoreText(FULL_TEST + MD_MARKER))
    }

}
