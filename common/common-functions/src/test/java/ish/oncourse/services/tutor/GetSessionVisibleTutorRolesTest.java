package ish.oncourse.services.tutor;

import ish.oncourse.model.Session;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetSessionVisibleTutorRolesTest {

    @Test
    public void test() {

        List roles = new ArrayList<>();
        // null finish date
        roles.add(mockTutorRole(null, 1L));
        // finish date in future
        roles.add(mockTutorRole(DateUtils.addDays(new Date(), 1), 2L));
        // finish date in now
        roles.add(mockTutorRole(DateUtils.addDays(new Date(), 0), 3L));
        // finisg date in past
        roles.add(mockTutorRole(DateUtils.addDays(new Date(), -1), 4L));

        Session session = mock(Session.class);
        ObjectContext objectContext = mock(ObjectContext.class);
        when(objectContext.select(Matchers.any())).thenReturn(roles);

        List<TutorRole> res = GetSessionVisibleTutorRoles.valueOf(objectContext, session).get();

        assertEquals(2, res.size());
        assertEquals("Only active tutors should be filtered (finish date == null || date.after(now)).",
                2, res.stream().filter(role -> role.getId() == 1L || role.getId() == 2L).count());
    }

    private TutorRole mockTutorRole(Date date, long roleId) {
        TutorRole role = mock(TutorRole.class);
        Tutor tutor = mock(Tutor.class);
        when(role.getTutor()).thenReturn(tutor);
        when(role.getId()).thenReturn(roleId);
        when(tutor.getFinishDate()).thenReturn(date);
        return role;
    }
}
