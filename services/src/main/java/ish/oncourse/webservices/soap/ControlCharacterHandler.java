package ish.oncourse.webservices.soap;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;

import java.io.IOException;
import java.io.Writer;

/**
 * Removes ASCII control characters from SOAP messages
 * C0 range: 0 - 0x1F
 * C1 range: 0x7F - 0x9F
 * 8859 range: A0, AD
 */
public class ControlCharacterHandler implements CharacterEscapeHandler {

    public void escape(char[] ch, int start, int length, boolean isAttVal, Writer out) throws IOException {
        int limit = start + length;
        for (int i = start; i < limit; i++) {
            char c = ch[i];
            if (c == '&' || c == '<' || c == '>' || c == '\r' || (c == '\"' && isAttVal)) {
                if (i != start)
                    out.write(removeControl(ch, start, i - start));
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
            out.write(removeControl(ch, start, limit - start));
    }

    private String removeControl(char[] ch, int start, int offset) {
        StringBuffer buf = new StringBuffer();

        for (int i = start; i < start + offset; i++) {
            if ((ch[i] > 0x1f && ch[i] < 0x7f) || ch[i] == '\r' || ch[i] > 0xa0) {
                buf.append(ch[i]);
            }
        }
        return buf.toString();
    }
}
