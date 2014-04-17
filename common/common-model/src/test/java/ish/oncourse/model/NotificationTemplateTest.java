package ish.oncourse.model;


import ish.oncourse.test.ContextUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.validation.ValidationException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class NotificationTemplateTest {

    /**
     * Entity to test.
     */
    private NotificationTemplate notificationTemplate;

    private ObjectContext context;

    @Before
    public void setup() throws Exception {
        ContextUtils.setupDataSources();
        context = ContextUtils.createObjectContext();
    }

    @Test
    public void testRequiredCollegeId() throws Exception {
        notificationTemplate = context.newObject(NotificationTemplate.class);
        assertNull("College status should be null before test", notificationTemplate.getCollege());

        boolean invalid = false;
        try {
            context.commitChanges();
        } catch (ValidationException ve) {
            invalid = true;
        }
        assertTrue("commit should be in failure status", invalid);
    }

}
