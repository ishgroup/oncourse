/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.builders

import groovy.transform.CompileStatic
import ish.common.types.SurveyVisibility
import ish.oncourse.commercial.replication.updaters.AngelUpdaterImpl
import ish.oncourse.commercial.replication.updaters.RelationShipCallback
import ish.oncourse.server.cayenne.*
import ish.oncourse.webservices.v23.stubs.replication.SurveyStub
import org.apache.cayenne.ObjectId
import org.junit.jupiter.api.Test

import static org.mockito.Mockito.*
import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class SurveyStubBuilderTest {


    @Test
    void test(){
        Survey survey = createMockSurvey()

        AngelStubBuilderImpl stubBuilder = new AngelStubBuilderImpl()
        SurveyStub stub = (SurveyStub) stubBuilder.convert(survey)

        assertEquals(survey.getComment(), stub.getComment(), "stub Comment")
        assertEquals(survey.getCourseScore(), stub.getCourseScore(), "stub CourseScore")
        assertEquals(survey.getTutorScore(), stub.getTutorScore(), "stub TutorScore")
        assertEquals(survey.getVenueScore(), stub.getVenueScore(), "stub VenueScore")
        assertEquals(survey.getEnrolment().getId(), stub.getEnrolmentId(), "stub Enrolment")
        assertEquals(survey.getVisibility().getDatabaseValue(), stub.getVisibility(), "stub visibility")

        AngelUpdaterImpl updater = new AngelUpdaterImpl()
        Survey surveyFromStub  = createMockSurvey()
        updater.updateEntityFromStub(stub, surveyFromStub, new RelationShipCallback() {
            @Override
            <M extends Queueable> M updateRelationShip(Long entityId, Class<M> clazz) {
                if (clazz == Enrolment.class) {
                    return (M) survey.getEnrolment()
                } else if (clazz == FieldConfiguration.class) {
                    return (M) survey.getFieldConfiguration()
                }
                return null
            }
        })
        assertEquals(survey.getComment(), surveyFromStub.getComment(),"stub Comment")
        assertEquals(survey.getCourseScore(), surveyFromStub.getCourseScore(), "stub CourseScore")
        assertEquals(survey.getTutorScore(), surveyFromStub.getTutorScore(), "stub TutorScore")
        assertEquals(survey.getVenueScore(), surveyFromStub.getVenueScore(), "stub VenueScore")
        assertEquals(survey.getEnrolment().getId(), surveyFromStub.getEnrolment().getId(), "stub Enrolment")


    }

    private static Survey createMockSurvey() {
        final Enrolment enrolment = mock(Enrolment)
        when(enrolment.getId()).thenReturn(1L)
        final FieldConfiguration fieldConfiguration = spy(new SurveyFieldConfiguration())
        fieldConfiguration.setId(1L)
        when(fieldConfiguration.getId()).thenCallRealMethod()
        Survey survey = mock(Survey)
        when(survey.getComment()).thenReturn('Comment')
        when(survey.getCourseScore()).thenReturn(1)
        when(survey.getTutorScore()).thenReturn(2)
        when(survey.getVenueScore()).thenReturn(3)
        when(survey.getEnrolment()).thenReturn(enrolment)
        when(survey.getVisibility()).thenReturn(SurveyVisibility.REVIEW)
        when(survey.getFieldConfiguration()).thenReturn(fieldConfiguration)
        when(survey.getObjectId()).thenReturn(new ObjectId("Survey", "id", 1L))
        survey
    }
}
