package ish.oncourse.webservices.soap;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;

import java.io.IOException;
import java.io.Writer;

public class CustomEscapeHandler implements CharacterEscapeHandler {

    private static final char TAB = 0x9;
    private static final char LF = 0xA;
    private static final char CR = 0xD;

    public void escape(char[] ch, int start, int length, boolean isAttVal, Writer out) throws IOException {
        int limit = start + length;
        for (int i = start; i < limit; i++) {
            char c = ch[i];
            if (c == '&' || c == '<' || c == '>' || (c == '\r' && !isAttVal) || (c == '\"' && isAttVal)) {
                if (i != start)
                    out.write(removeXMLInvalid(ch, start, i - start, isAttVal));
                start = i + 1;
                switch (ch[i]) {
                    case '&':
                        out.write("&amp;");
                        break;
                    case '<':
                        out.write("&lt;");
                        break;
                    case '>':
                        out.write("&gt;");
                        break;
                    case '\"':
                        out.write("&quot;");
                        break;
                }
            }
        }
        if (start != limit)
            out.write(removeXMLInvalid(ch, start, limit - start, isAttVal));
    }

    private String removeXMLInvalid(char[] ch, int start, int offset, boolean isAttr) {
        StringBuffer buf = new StringBuffer();

        for (int i = start; i < start + offset; i++) {
            char c = ch[i];

            // xml-valid characters: #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]

            if (c > 0x1F && c < Character.MIN_HIGH_SURROGATE) {
                buf.append(c);
            } else if (c == TAB || c == LF || c == CR) {
                if (isAttr) {
                    buf.append("&#");
                    buf.append((int) c);
                    buf.append(';');
                } else {
                    buf.append(c);
                }
            } else if ((c > Character.MAX_LOW_SURROGATE && c < 0xFFFE) ||
                    (c >= Character.MIN_SUPPLEMENTARY_CODE_POINT && c <= Character.MAX_CODE_POINT)) {
                buf.append(c);
            }
        }
        return buf.toString();
    }
}