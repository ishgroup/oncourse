package ish.oncourse.services.tutor;

import ish.oncourse.model.Tutor;
import ish.oncourse.services.tutor.GetIsActiveTutor;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetIsActiveTutorTest {

    @Test
    public void test() {
        assertFalse(GetIsActiveTutor.valueOf(prepareTutor(-5)).get());
        assertFalse(GetIsActiveTutor.valueOf(prepareTutor(0)).get());
        assertTrue(GetIsActiveTutor.valueOf(prepareTutor(5)).get());
    }

    @Test(expected = NullPointerException.class)
    public void testNull() {
        assertFalse(GetIsActiveTutor.valueOf(null).get());
    }

    public Tutor prepareTutor(int addDaysToNow) {
        Tutor t = mock(Tutor.class);

        Date finishDate = DateUtils.addDays(new Date(), addDaysToNow);
        when(t.getFinishDate()).thenReturn(finishDate);
        return t;
    }
}
