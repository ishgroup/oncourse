package ish.oncourse.webservices.soap;

import org.junit.Test;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ControlCharacterHandlerTest {

    @Test
    public void testC0() throws IOException {
        char[] group = {0, 0x1F, 0x20};

        try(Writer w = new CharArrayWriter()) {
            ControlCharacterHandler handler = new ControlCharacterHandler();
            handler.escape(group.toString().toCharArray(), 0, group.length, false, w);
            assertEquals("[C@", w.toString());
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void testC1() throws IOException {
        char[] group = {0x7E, 0x7F, 0x9F, 0xA0, 0xA1};

        try(Writer w = new CharArrayWriter()) {
            ControlCharacterHandler handler = new ControlCharacterHandler();
            handler.escape(group.toString().toCharArray(), 0, group.length, false, w);
            assertEquals("[C@3d", w.toString());
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void test8859() throws IOException {
        char[] group = {0x9F, 0xA0, 0xA1, 0xAC, 0xAD, 0xAE};

        try(Writer w = new CharArrayWriter()) {
            ControlCharacterHandler handler = new ControlCharacterHandler();
            handler.escape(group.toString().toCharArray(), 0, group.length, false, w);
            assertEquals("[C@349", w.toString());
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void escapeTagValueTest() throws IOException {

        StringBuilder strb = new StringBuilder();
        strb.append("<replication =\"abcdef");
        strb.append(getControlChars());
        strb.append("\">value");
        strb.append(getControlChars());
        strb.append("</replication");
        strb.append(getControlChars());
        strb.append('>');

        try(Writer w = new CharArrayWriter()) {
            ControlCharacterHandler handler = new ControlCharacterHandler();
            handler.escape(strb.toString().toCharArray(), 0, strb.toString().length(), false, w);
            assertEquals("&lt;replication =\"abcdef\"&gt;value&lt;/replication&gt;", w.toString());
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    public void escapeTagAttributeTest() throws IOException {

        StringBuilder strb = new StringBuilder();
        strb.append("<replication =\"abcdef");
        strb.append(getControlChars());
        strb.append("\">value");
        strb.append(getControlChars());
        strb.append("</replication");
        strb.append(getControlChars());
        strb.append('>');

        try(Writer w = new CharArrayWriter()) {
            ControlCharacterHandler handler = new ControlCharacterHandler();
            handler.escape(strb.toString().toCharArray(), 0, strb.toString().length(), true, w);
            assertEquals("&lt;replication =&quot;abcdef&quot;&gt;value&lt;/replication&gt;", w.toString());
        } catch (Exception ex) {
            fail();
        }
    }

    private char[] getControlChars() {
        StringBuilder res = new StringBuilder();

        for(int i = 0; i < 32; i++) {
            res.append((char) i);
        }

        for(int i = 127; i < 161; i++) {
            res.append((char) i);
        }
        return res.toString().toCharArray();
    }
}
