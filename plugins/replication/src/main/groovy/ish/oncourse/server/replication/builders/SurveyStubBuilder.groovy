/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.Survey
import ish.oncourse.webservices.v21.stubs.replication.SurveyStub

class SurveyStubBuilder  extends AbstractAngelStubBuilder<Survey, SurveyStub> {
    @Override
    SurveyStub createFullStub(Survey entity) {
        def surveyStub = new SurveyStub()
        surveyStub.setModified(entity.getModifiedOn())
        surveyStub.setCreated(entity.getCreatedOn())
        surveyStub.setComment(entity.getComment())
        surveyStub.setTestimonial(entity.getTestimonial())
        surveyStub.setCourseScore(entity.getCourseScore())
        surveyStub.setTutorScore(entity.getTutorScore())
        surveyStub.setVenueScore(entity.getVenueScore())
        surveyStub.setNetPromoterScore(entity.getNetPromoterScore())
        surveyStub.setEnrolmentId(entity.getEnrolment().getId())
		surveyStub.setVisibility(entity.getVisibility().getDatabaseValue())
		surveyStub.setFieldConfigurationId(entity.getFieldConfiguration().getId())
        return surveyStub
    }
}
