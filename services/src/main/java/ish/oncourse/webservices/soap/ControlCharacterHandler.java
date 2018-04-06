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
    @Override
    public void escape(char[] ch, int start, int length, boolean isAttVal, Writer out) throws IOException {
        for (int i = start; i < start + length; i++) {
            if ((ch[i] > 0x1F && ch[i] < 0x7F) || (ch[i] > 0x9F && ch[i] != 0xA0 && ch[i] != 0xAD)) {
                out.write(ch[i]);
            }
        }
    }
}
