package ish.oncourse.model;

import ish.oncourse.test.ContextUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.validation.ValidationException;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SessionTutorTest {

    /**
     * Entity to test.
     */
    private SessionTutor sessionTutor;

    private ObjectContext context;
    private College college;
    private Session session;
    private Tutor tutor;


    @Before
    public void setup() throws Exception {
        ContextUtils.setupDataSources();
        context = ContextUtils.createObjectContext();

        /**
         * Instance of any College entity.
         */
        college = context.newObject(College.class);
        college.setTimeZone("Australia/Sydney");
        college.setFirstRemoteAuthentication(new Date());
        college.setRequiresAvetmiss(false);

        context.commitChanges();
        /**
         * Instance of any Session entity.
         */
        session = context.newObject(Session.class);
        session.setCollege(college);

        context.commitChanges();
        /**
         * Instance of any Tutor entity.
         */
        tutor = context.newObject(Tutor.class);
        tutor.setCollege(college);

        context.commitChanges();
    }


    @Test
    public void testNotNullCollegeId() throws Exception {
        sessionTutor = context.newObject(SessionTutor.class);
        assertNull("College status should be null before test", sessionTutor.getCollege());

        /**
         *  Tutor and Session is required too
         */
        sessionTutor.setTutor(tutor);
        sessionTutor.setSession(session);

        boolean invalid = false;
        try {
            context.commitChanges();
        } catch (ValidationException ve) {
            invalid = true;
        }
        assertTrue("commit should be in failure status", invalid);
    }
}
