package ish.oncourse.webservices.replication.v6.builders;

import ish.oncourse.model.Survey;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v6.stubs.replication.SurveyStub;

public class SurveyStubBuilder extends AbstractWillowStubBuilder<Survey, SurveyStub> {
    @Override
    protected SurveyStub createFullStub(Survey entity) {
        SurveyStub surveyStub = new SurveyStub();
        surveyStub.setModified(entity.getModified());
        surveyStub.setCreated(entity.getCreated());
        surveyStub.setComment(entity.getComment());
        surveyStub.setCourseScore(entity.getCourseScore());
        surveyStub.setTutorScore(entity.getTutorScore());
        surveyStub.setUniqueCode(entity.getUniqueCode());
        surveyStub.setVenueScore(entity.getVenueScore());
        surveyStub.setEnrolmentId(entity.getEnrolment().getId());
        return surveyStub;
    }
}
