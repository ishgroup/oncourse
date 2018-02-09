package ish.oncourse.webservices.replication.v10.builders;

import ish.common.types.SurveyVisibility;
import ish.oncourse.model.Survey;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v10.stubs.replication.SurveyStub;

public class SurveyStubBuilder extends AbstractWillowStubBuilder<Survey, SurveyStub> {
    @Override
    protected SurveyStub createFullStub(Survey entity) {
        SurveyStub surveyStub = new SurveyStub();
        surveyStub.setModified(entity.getModified());
        surveyStub.setCreated(entity.getCreated());
        surveyStub.setComment(entity.getComment());
        surveyStub.setCourseScore(entity.getCourseScore());
        surveyStub.setTutorScore(entity.getTutorScore());
        surveyStub.setUniqueCode("00000000");
        surveyStub.setVenueScore(entity.getVenueScore());
        surveyStub.setEnrolmentId(entity.getEnrolment().getId());
        surveyStub.setPublicComment(SurveyVisibility.TESTIMONIAL.equals(entity.getVisibility()));
        return surveyStub;
    }
}
