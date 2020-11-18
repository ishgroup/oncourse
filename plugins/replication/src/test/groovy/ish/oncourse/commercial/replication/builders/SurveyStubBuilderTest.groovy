package ish.oncourse.commercial.replication.builders

import groovy.transform.CompileStatic
import ish.common.types.SurveyVisibility
import ish.oncourse.commercial.replication.updaters.AngelUpdaterImpl
import ish.oncourse.commercial.replication.updaters.RelationShipCallback
import ish.oncourse.server.cayenne.*
import ish.oncourse.webservices.v22.stubs.replication.SurveyStub
import org.apache.cayenne.ObjectId
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.*

@CompileStatic
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
