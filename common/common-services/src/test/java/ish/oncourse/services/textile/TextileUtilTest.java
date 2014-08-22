package ish.oncourse.services.textile;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TextileUtilTest {
    @Test
    public void test_unicodeQuotesEncoding() {
        // « (\u00AB) in UTF-8
        // » (\u00BB) in UTF-8
        String result = TextileUtil.unicodeQuotesEncoding("{page code:«34»}");
        assertEquals("Unicode Quotes were replaced", "{page code:\"34\"}", result);
        // “ (\u201C) in UTF-8
        // ” (\u201D) in UTF-8
        result = TextileUtil.unicodeQuotesEncoding("{page code:“34”}");
        assertEquals("Unicode Quotes were replaced", "{page code:\"34\"}", result);
        // „ (\u201E) in UTF-8
        // ‟ (\u201F) in UTF-8
        result = TextileUtil.unicodeQuotesEncoding("{page code:„34‟}");
        assertEquals("Unicode Quotes were replaced", "{page code:\"34\"}", result);
        // “ (\u0093) in UTF-8
        // ” (\u0094) in UTF-8
        result = TextileUtil.unicodeQuotesEncoding("{page code:“34”}");
        assertEquals("Unicode Quotes were replaced", "{page code:\"34\"}", result);

        // ‘ (\u2018) in UTF-8
        // ’ (\u2019) in UTF-8
        result = TextileUtil.unicodeQuotesEncoding("{page code:‘34’}");
        assertEquals("Unicode Quotes were replaced", "{page code:\'34\'}", result);
        // ‚ (\u201A) in UTF-8
        // ‛ (\u201B) in UTF-8
        result = TextileUtil.unicodeQuotesEncoding("{page code:‚34‛}");
        assertEquals("Unicode Quotes were replaced", "{page code:\'34\'}", result);
        // ‹ (\u2039) in UTF-8
        // › (\u203A) in UTF-8
        result = TextileUtil.unicodeQuotesEncoding("{page code:‹34›}");
        assertEquals("Unicode Quotes were replaced", "{page code:\'34\'}", result);
    }
}
