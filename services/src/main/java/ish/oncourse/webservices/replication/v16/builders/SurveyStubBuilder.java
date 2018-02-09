package ish.oncourse.webservices.replication.v16.builders;

import ish.common.types.SurveyVisibility;
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
        surveyStub.setTestimonial(entity.getTestimonial());
        surveyStub.setCourseScore(entity.getCourseScore());
        surveyStub.setTutorScore(entity.getTutorScore());
        surveyStub.setVenueScore(entity.getVenueScore());
        surveyStub.setNetPromoterScore(entity.getNetPromoterScore());
        surveyStub.setEnrolmentId(entity.getEnrolment().getId());
        surveyStub.setPublicComment(SurveyVisibility.TESTIMONIAL.equals(entity.getVisibility()));
        return surveyStub;
    }
}
