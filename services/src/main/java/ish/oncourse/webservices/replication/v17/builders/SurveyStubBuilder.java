package ish.oncourse.webservices.replication.v17.builders;

import ish.oncourse.model.Survey;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v17.stubs.replication.SurveyStub;

public class SurveyStubBuilder extends AbstractWillowStubBuilder<Survey, SurveyStub> {
    @Override
    protected SurveyStub createFullStub(Survey entity) {
        SurveyStub surveyStub = new SurveyStub();
        surveyStub.setModified(entity.getModified());
        surveyStub.setCreated(entity.getCreated());
        surveyStub.setComment(entity.getComment());
        surveyStub.setTestimonial(entity.getTestimonial());
        surveyStub.setCourseScore(entity.getCourseScore());
        surveyStub.setTutorScore(entity.getTutorScore());
        surveyStub.setVenueScore(entity.getVenueScore());
        surveyStub.setNetPromoterScore(entity.getNetPromoterScore());
        surveyStub.setEnrolmentId(entity.getEnrolment().getId());
        surveyStub.setVisibility(entity.getVisibility().getDatabaseValue());
        return surveyStub;
    }
}
