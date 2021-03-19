package ish.oncourse.ui.utils;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetVisibleTutorsTest {

    @Test
    public void test() {
        CourseClass cc = mock(CourseClass.class);

        GetVisibleTutors getter = mock(GetVisibleTutors.class);
        List<TutorRole> roles = prepareRoles();
        when(getter.getRoles()).thenReturn(roles);
        doCallRealMethod().when(getter).get();

        List<Tutor> res = getter.get();

        assertEquals(1, res.size());
        assertEquals(1, (long) res.get(0).getId());
    }

    private List<TutorRole> prepareRoles() {
        Date current = new Date();

        List<TutorRole> res = new ArrayList<>();

        Tutor tutor = mock(Tutor.class);
        when(tutor.getFinishDate()).thenReturn(DateUtils.addDays(current, 5));
        when(tutor.getId()).thenReturn(1L);

        TutorRole role = mock(TutorRole.class);
        when(role.getTutor()).thenReturn(tutor);
        res.add(role);

        tutor = mock(Tutor.class);
        when(tutor.getFinishDate()).thenReturn(new Date());
        when(tutor.getId()).thenReturn(2L);

        role = mock(TutorRole.class);
        when(role.getTutor()).thenReturn(tutor);
        res.add(role);

        tutor = mock(Tutor.class);
        when(tutor.getFinishDate()).thenReturn(DateUtils.addDays(current, -5));
        when(tutor.getId()).thenReturn(3L);

        role = mock(TutorRole.class);
        when(role.getTutor()).thenReturn(tutor);
        res.add(role);

        return res;
    }
}
