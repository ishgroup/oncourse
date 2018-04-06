package ish.oncourse.webservices.soap;

import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.Writer;

public class ControlCharacterHandlerTest {

    @Test
    public void testC0() throws IOException {
        char[] group = {0, 0x1F, 0x20};

        Writer w = mock(Writer.class);

        ControlCharacterHandler handler = new ControlCharacterHandler();
        handler.escape(group, 0, group.length, false, w);

        verify(w).write(0x20);
    }

    @Test
    public void testC1() throws IOException {
        char[] group = {0x7E, 0x7F, 0x9F, 0xA0, 0xA1};

        Writer w = mock(Writer.class);

        ControlCharacterHandler handler = new ControlCharacterHandler();
        handler.escape(group, 0, group.length, false, w);

        verify(w).write(0x7E);
        verify(w).write(0xA1);
    }

    @Test
    public void test8859() throws IOException {
        char[] group = {0x9F, 0xA0, 0xA1, 0xAC, 0xAD, 0xAE};

        Writer w = mock(Writer.class);

        ControlCharacterHandler handler = new ControlCharacterHandler();
        handler.escape(group, 0, group.length, false, w);

        verify(w).write(0xA1);
        verify(w).write(0xAC);
        verify(w).write(0xAE);
    }
}
