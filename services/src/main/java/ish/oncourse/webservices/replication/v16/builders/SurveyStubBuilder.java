package ish.oncourse.webservices.replication.v16.builders;

import ish.oncourse.model.Survey;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v16.stubs.replication.SurveyStub;

public class SurveyStubBuilder extends AbstractWillowStubBuilder<Survey, SurveyStub> {
    @Override
    protected SurveyStub createFullStub(Survey entity) {
        SurveyStub surveyStub = new SurveyStub();
        surveyStub.setModified(entity.getModified());
        surveyStub.setCreated(entity.getCreated());
        surveyStub.setComment(entity.getComment());
        surveyStub.setCourseScore(entity.getCourseScore());
        surveyStub.setTutorScore(entity.getTutorScore());
        surveyStub.setVenueScore(entity.getVenueScore());
        surveyStub.setEnrolmentId(entity.getEnrolment().getId());
		if (entity.getPublicComment() != null) {
			surveyStub.setPublicComment(entity.getPublicComment());
		}
        return surveyStub;
    }
}
