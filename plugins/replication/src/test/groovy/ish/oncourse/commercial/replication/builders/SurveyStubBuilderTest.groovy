package ish.oncourse.commercial.replication.builders

import ish.common.types.SurveyVisibility
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.replication.updaters.AngelUpdaterImpl
import ish.oncourse.server.replication.updaters.RelationShipCallback
import ish.oncourse.webservices.v22.stubs.replication.SurveyStub
import org.apache.cayenne.ObjectId
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.*

class SurveyStubBuilderTest {


    @Test
    void test(){
        Survey survey = createMockSurvey()

        AngelStubBuilderImpl stubBuilder = new AngelStubBuilderImpl()
        SurveyStub stub = (SurveyStub) stubBuilder.convert(survey)

        assertEquals("stub Comment", survey.getComment(), stub.getComment())
        assertEquals("stub CourseScore", survey.getCourseScore(), stub.getCourseScore())
        assertEquals("stub TutorScore", survey.getTutorScore(), stub.getTutorScore())
        assertEquals("stub VenueScore", survey.getVenueScore(), stub.getVenueScore())
        assertEquals("stub Enrolment", survey.getEnrolment().getId(), stub.getEnrolmentId())
        assertEquals("stub visibility", survey.getVisibility().getDatabaseValue(), stub.getVisibility())

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
        assertEquals("stub Comment", survey.getComment(), surveyFromStub.getComment())
        assertEquals("stub CourseScore", survey.getCourseScore(), surveyFromStub.getCourseScore())
        assertEquals("stub TutorScore", survey.getTutorScore(), surveyFromStub.getTutorScore())
        assertEquals("stub VenueScore", survey.getVenueScore(), surveyFromStub.getVenueScore())
        assertEquals("stub Enrolment", survey.getEnrolment().getId(), surveyFromStub.getEnrolment().getId())


    }

    private static Survey createMockSurvey() {
        final Enrolment enrolment = org.mockito.Mockito.mock(Enrolment)
        org.mockito.Mockito.when(enrolment.getId()).thenReturn(1L)
        final FieldConfiguration fieldConfiguration = org.mockito.Mockito.spy(new SurveyFieldConfiguration())
        fieldConfiguration.setId(1L)
        org.mockito.Mockito.when(fieldConfiguration.getId()).thenCallRealMethod()
        Survey survey = org.mockito.Mockito.mock(Survey)
        org.mockito.Mockito.when(survey.getComment()).thenReturn('Comment')
        org.mockito.Mockito.when(survey.getCourseScore()).thenReturn(1)
        org.mockito.Mockito.when(survey.getTutorScore()).thenReturn(2)
        org.mockito.Mockito.when(survey.getVenueScore()).thenReturn(3)
        org.mockito.Mockito.when(survey.getEnrolment()).thenReturn(enrolment)
        org.mockito.Mockito.when(survey.getVisibility()).thenReturn(SurveyVisibility.REVIEW)
        org.mockito.Mockito.when(survey.getFieldConfiguration()).thenReturn(fieldConfiguration)
        org.mockito.Mockito.when(survey.getObjectId()).thenReturn(new ObjectId("Survey", "id", 1L))
        survey
    }
}
