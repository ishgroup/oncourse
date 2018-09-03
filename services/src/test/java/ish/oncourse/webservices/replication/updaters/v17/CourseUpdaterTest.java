package ish.oncourse.webservices.replication.updaters.v17;

import ish.oncourse.model.Course;
import ish.oncourse.model.FieldConfigurationScheme;
import ish.oncourse.services.textile.TextileConverter;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.replication.v17.updaters.CourseUpdater;
import ish.oncourse.webservices.v17.stubs.replication.CourseStub;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CourseUpdaterTest{

    private CourseUpdater updater;
    private Course course;
    private RelationShipCallback relationship;

    @Before
    public void setup() {
        TextileConverter texConv = mock(TextileConverter.class);

        relationship = mock(RelationShipCallback.class);
        when(relationship.updateRelationShip(0L, FieldConfigurationScheme.class)).thenReturn(null);

        updater = new CourseUpdater(texConv);

        course = mock(Course.class);
    }

    /**
     * Check for correct update nullable qualification in course object
     * @throws Exception exception
     */
    @Test
    public void updateEntityByNullQualification() throws Exception {
        CourseStub courseStub = mock(CourseStub.class);
        when(courseStub.getFieldConfigurationSchemeId()).thenReturn(0L);


        when(courseStub.getQualificationId()).thenReturn(null);
        updater.updateEntityFromStub(courseStub, course, relationship);

        assertNull(course.getQualification());
    }

    /**
     * Check for correct update nullable nominal hours in course object
     * @throws Exception exception
     */
    @Test
    public void updateEntityByNullNominalHours() throws Exception {
        CourseStub courseStub = mock(CourseStub.class);
        when(courseStub.getFieldConfigurationSchemeId()).thenReturn(0L);

        when(courseStub.getQualificationId()).thenReturn(null);
        when(courseStub.getNominalHours()).thenReturn(null);
        updater.updateEntityFromStub(courseStub, course, relationship);

        assertEquals((Float) 0F, (Float)course.getNominalHours());
    }
}