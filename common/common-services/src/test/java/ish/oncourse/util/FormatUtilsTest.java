package ish.oncourse.util;

import ish.oncourse.model.Session;
import org.junit.Test;
import org.mockito.Mockito;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static ish.oncourse.util.FormatUtils.*;
import static org.junit.Assert.*;

/**
 * Created by akoira on 12/16/16.
 */
public class FormatUtilsTest {

    @Test
    public void test_getSessionTimeAsString() throws ParseException {
        Date start = new SimpleDateFormat("h:mm a").parse("9:30 am");
        Date end = new SimpleDateFormat("h a").parse("10 am");

        Session session = Mockito.mock(Session.class);
        Mockito.when(session.getStartDate()).thenReturn(start);
        Mockito.when(session.getEndDate()).thenReturn(end);
        Mockito.when(session.getTimeZone()).thenReturn("Australia/Sydney");

        assertEquals("9:30 am - 10 am", getSessionTimeAsString(session));


        start = new SimpleDateFormat("h:mm a").parse("9:30 pm");
        end = new SimpleDateFormat("h a").parse("10 pm");
        session = Mockito.mock(Session.class);
        Mockito.when(session.getStartDate()).thenReturn(start);
        Mockito.when(session.getEndDate()).thenReturn(end);
        Mockito.when(session.getTimeZone()).thenReturn("Australia/Sydney");
        assertEquals("9:30 pm - 10 pm", getSessionTimeAsString(session));
    }
}
