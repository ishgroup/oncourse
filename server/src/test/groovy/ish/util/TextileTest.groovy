/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@CompileStatic
class TextileTest {

    @Test
    void testSimpleText() {
        String input = "some text"
        String expected = "<p>some text</p>"

        Assertions.assertEquals(expected, Textile.process(input).trim())

        input = "some text with" + RuntimeUtil.LINE_SEPARATOR + "new line"
        expected = "<p>some text with<br/>new line</p>"

        Assertions.assertEquals(expected, Textile.process(input).trim())
    }

    @Test
    void testSimpleBold() {
        String input = "some *bold* text"
        String expected = "<p>some <strong>bold</strong> text</p>"

        Assertions.assertEquals(expected, Textile.process(input).trim())
    }

    @Test
    void testHeader() {
        String input = "h4. header"
        String expected = "<h4 id=\"header\">header</h4>"

        Assertions.assertEquals(expected, Textile.process(input).trim())
    }

    @Test
    void testSimpleLink() {
        String input = "\"A Hyperlink\":www.ish.com.au"
        String expected = "<p><a href=\"www.ish.com.au\">A Hyperlink</a></p>"

        Assertions.assertEquals(expected, Textile.process(input).trim())
    }

    @Test
    void testSimpleEmailLink() {
        String input = "\"A mail to link\":mailto:test@ish.com.au"
        String expected = "<p><a href=\"mailto:test@ish.com.au\">A mail to link</a></p>"

        Assertions.assertEquals(expected, Textile.process(input).trim())
    }

    @Test
    void testSimpleCode() {
        String input = "@some code@"
        String expected = "<p><code>some code</code></p>"

        Assertions.assertEquals(expected, Textile.process(input).trim())
    }

    @Test
    void testSimpleBlockqoute() {
        String input = "bq. This text will be enclosed in an HTML blockquote element."
        String expected = "<blockquote><p>This text will be enclosed in an HTML blockquote element.</p></blockquote>"

        Assertions.assertEquals(expected, Textile.process(input).trim())
    }

    @Test
    void testSimpleSuperscript() {
        String input = "Brand ^TM^"
        String expected = "<p>Brand <sup>TM</sup></p>"

        Assertions.assertEquals(expected, Textile.process(input).trim())
    }

    @Test
    void testSimpleSubscript() {
        String input = "Text ~subscript~"
        String expected = "<p>Text <sub>subscript</sub></p>"

        Assertions.assertEquals(expected, Textile.process(input).trim())
    }

    @Test
    void testSimpleTable() {
        String input = "|_. Header |_. Header |_. Header |" + RuntimeUtil.LINE_SEPARATOR
        input = input + "| Cell 1 | Cell 2 | Cell 3 |" + RuntimeUtil.LINE_SEPARATOR
        input = input + "| Cell 1 | Cell 2 | Cell 3 |" + RuntimeUtil.LINE_SEPARATOR

        String expected = "<table><tr><th>Header </th><th>Header </th><th>Header </th></tr>"
        expected = expected + "<tr><td>Cell 1 </td><td>Cell 2 </td><td>Cell 3 </td></tr>"
        expected = expected + "<tr><td>Cell 1 </td><td>Cell 2 </td><td>Cell 3 </td></tr></table>"

        Assertions.assertEquals(expected, Textile.process(input).trim())
    }

    @Test
    void testBullets() {

        String input = "* bullet one" + RuntimeUtil.LINE_SEPARATOR
        input = input + "* bullet two" + RuntimeUtil.LINE_SEPARATOR
        input = input + "* bullet three" + RuntimeUtil.LINE_SEPARATOR

        String expected = "<ul>"
        expected = expected + "<li>bullet one</li>"
        expected = expected + "<li>bullet two</li>"
        expected = expected + "<li>bullet three</li>"
        expected = expected + "</ul>"

        Assertions.assertEquals(expected, Textile.process(input).trim())
    }

    @Test
    void testNumberedBullets() {

        String input = "# bullet 1" + RuntimeUtil.LINE_SEPARATOR
        input = input + "# bullet 2" + RuntimeUtil.LINE_SEPARATOR
        input = input + "# bullet 3" + RuntimeUtil.LINE_SEPARATOR

        String expected = "<ol>"
        expected = expected + "<li>bullet 1</li>"
        expected = expected + "<li>bullet 2</li>"
        expected = expected + "<li>bullet 3</li>"
        expected = expected + "</ol>"

        Assertions.assertEquals(expected, Textile.process(input).trim())
    }

    @Test
    void testProcessToJasperHtml() {
        StringBuilder input =
                new StringBuilder()
                        .append("some text 1 {block name:“public speaking bootcamp slider”} some text 2")
                        .append(RuntimeUtil.LINE_SEPARATOR)
                        .append("{some textile code name:“another speaking bootcamp slider”} some text 3")
                        .append(RuntimeUtil.LINE_SEPARATOR)
        String expected = "<p>some text 1  some text 2<br/> some text 3</p>"
        Assertions.assertEquals(expected, Textile.processToJasperHtml(input.toString()).trim())
    }

    @Test
    void testComplexText() {

        String input = "some text" + RuntimeUtil.LINE_SEPARATOR
        input = input + "* bullet one" + RuntimeUtil.LINE_SEPARATOR
        input = input + "* bullet two" + RuntimeUtil.LINE_SEPARATOR
        input = input + "* bullet three" + RuntimeUtil.LINE_SEPARATOR
        input = input + "lorem ipsum lorem" + RuntimeUtil.LINE_SEPARATOR
        input = input + "lorem *ipsum* lorem" + RuntimeUtil.LINE_SEPARATOR
        input = input + "lorem *ipsum* _lorem_" + RuntimeUtil.LINE_SEPARATOR
        input = input + "lorem *ipsum lorem*" + RuntimeUtil.LINE_SEPARATOR
        input = input + "_lorem ipsum_ lorem" + RuntimeUtil.LINE_SEPARATOR
        input = input + "lorem ipsum lorem" + RuntimeUtil.LINE_SEPARATOR
        input = input + "# bullet 1" + RuntimeUtil.LINE_SEPARATOR
        input = input + "# bullet 2" + RuntimeUtil.LINE_SEPARATOR
        input = input + "# bullet 3" + RuntimeUtil.LINE_SEPARATOR
        input = input + "*lorem ipsum lorem*" + RuntimeUtil.LINE_SEPARATOR
        input = input + "lorem *ipsum* lorem" + RuntimeUtil.LINE_SEPARATOR

        String expected = "<p>some text</p>"
        expected = expected + "<ul>"
        expected = expected + "<li>bullet one</li>"
        expected = expected + "<li>bullet two</li>"
        expected = expected + "<li>bullet three</li>"
        expected = expected + "</ul>"
        expected = expected + "<p>lorem ipsum lorem<br/>"
        expected = expected + "lorem <strong>ipsum</strong> lorem<br/>"
        expected = expected + "lorem <strong>ipsum</strong> <em>lorem</em><br/>"
        expected = expected + "lorem <strong>ipsum lorem</strong><br/>"
        expected = expected + "<em>lorem ipsum</em> lorem<br/>"
        expected = expected + "lorem ipsum lorem</p>"
        expected = expected + "<ol>"
        expected = expected + "<li>bullet 1</li>"
        expected = expected + "<li>bullet 2</li>"
        expected = expected + "<li>bullet 3</li>"
        expected = expected + "</ol>"
        expected = expected + "<p><strong>lorem ipsum lorem</strong><br/>"
        expected = expected + "lorem <strong>ipsum</strong> lorem</p>"

        Assertions.assertEquals(expected, Textile.process(input).trim())
    }

    @Test
    void testComplex2() {
        String input = "h4. Important information" + RuntimeUtil.LINE_SEPARATOR + RuntimeUtil.LINE_SEPARATOR
        input = input + "* So that individual needs can be met students will be learning from a self-paced manual." + RuntimeUtil.LINE_SEPARATOR
        input = input + "** The computer lab can be hot - it is recommended that you wear layers." + RuntimeUtil.LINE_SEPARATOR
        input = input + "* Children must not accompany adults to Adult Education classes." + RuntimeUtil.LINE_SEPARATOR
        input = input + "** Access to some classrooms at this venue is via a set of stairs." + RuntimeUtil.LINE_SEPARATOR

        input = input + "*some bold text*" + RuntimeUtil.LINE_SEPARATOR

        input = input + "_some italic text_" + RuntimeUtil.LINE_SEPARATOR

        input = input + "\"A Hyperlink\":www.ish.com.au" + RuntimeUtil.LINE_SEPARATOR

        input = input + "\"A mail to link\":mailto:test@ish.com.au" + RuntimeUtil.LINE_SEPARATOR

        input = input + "|_. Header |_. Header |_. Header |" + RuntimeUtil.LINE_SEPARATOR
        input = input + "| Cell 1 | Cell 2 | Cell 3 |" + RuntimeUtil.LINE_SEPARATOR
        input = input + "| Cell 1 | Cell 2 | Cell 3 |" + RuntimeUtil.LINE_SEPARATOR

        input = input + "@some code@" + RuntimeUtil.LINE_SEPARATOR + RuntimeUtil.LINE_SEPARATOR

        input = input + "bq. This text will be enclosed in an HTML blockquote element." + RuntimeUtil.LINE_SEPARATOR + RuntimeUtil.LINE_SEPARATOR

        input = input + "Brand ^TM^" + RuntimeUtil.LINE_SEPARATOR

        input = input + "Text ~subscript~" + RuntimeUtil.LINE_SEPARATOR

        String expected = "<h4 id=\"Importantinformation\">Important information</h4>"
        expected = expected + "<ul>"
        expected = expected + "<li>So that individual needs can be met students will be learning from a self-paced manual."
        expected = expected + "<ul><li>The computer lab can be hot &#8211; it is recommended that you wear layers.</li></ul></li>"
        expected = expected + "<li>Children must not accompany adults to Adult Education classes."
        expected = expected + "<ul><li>Access to some classrooms at this venue is via a set of stairs.</li></ul></li></ul>"
        expected = expected + "<p><strong>some bold text</strong><br/>"
        expected = expected + "<em>some italic text</em><br/>"
        expected = expected + "<a href=\"www.ish.com.au\">A Hyperlink</a><br/>"
        expected = expected + "<a href=\"mailto:test@ish.com.au\">A mail to link</a></p>"
        expected = expected + "<table><tr><th>Header </th><th>Header </th><th>Header </th></tr>"
        expected = expected + "<tr><td>Cell 1 </td><td>Cell 2 </td><td>Cell 3 </td></tr>"
        expected = expected + "<tr><td>Cell 1 </td><td>Cell 2 </td><td>Cell 3 </td></tr></table>"
        expected = expected + "<p><code>some code</code></p>"
        expected = expected + "<blockquote><p>This text will be enclosed in an HTML blockquote element.</p></blockquote><p>"
        expected = expected + "Brand <sup>TM</sup><br/>"
        expected = expected + "Text <sub>subscript</sub></p>"

        Assertions.assertEquals(expected, Textile.process(input).trim())
    }
}
