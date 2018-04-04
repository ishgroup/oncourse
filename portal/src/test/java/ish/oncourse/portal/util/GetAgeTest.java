package ish.oncourse.portal.util;

import ish.oncourse.model.Contact;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class GetAgeTest {

    @Test
    public void test() {
        Contact c = Mockito.mock(Contact.class);

        when(c.getDateOfBirth()).thenReturn(addYearsToNow(-5));
        assertEquals(5, (int) GetAge.valueOf(c).get());

        when(c.getDateOfBirth()).thenReturn(addYearsToNow(0));
        assertEquals(0, (int) GetAge.valueOf(c).get());

        when(c.getDateOfBirth()).thenReturn(addYearsToNow(5));
        assertEquals(-5, (int) GetAge.valueOf(c).get());

        when(c.getDateOfBirth()).thenReturn(null);
        assertEquals(null, GetAge.valueOf(c).get());
    }

    private Date addYearsToNow(int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, offset);
        return calendar.getTime();
    }
}
