package ish.oncourse.ui.utils;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Tutor;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetTutorNameTest {

    @Test
    public void test() {
        assertEquals("given family", GetTutorName.valueOf(prepareTutor("given", "family", false)).get());
        assertEquals("family", GetTutorName.valueOf(prepareTutor("given", "family", true)).get());

        assertEquals(StringUtils.EMPTY, GetTutorName.valueOf(prepareTutor(null, null, true)).get());
        assertEquals(StringUtils.EMPTY, GetTutorName.valueOf(prepareTutor(null, null, false)).get());
    }

    private Tutor prepareTutor(String firstName, String lastName, boolean isCompany) {
        Contact c = mock(Contact.class);
        when(c.getGivenName()).thenReturn(firstName);
        when(c.getFamilyName()).thenReturn(lastName);
        when(c.getIsCompany()).thenReturn(isCompany);

        Tutor t = mock(Tutor.class);
        when(t.getContact()).thenReturn(c);
        return t;
    }
}
