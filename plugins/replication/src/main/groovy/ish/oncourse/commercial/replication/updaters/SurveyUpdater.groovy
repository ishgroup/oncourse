/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.updaters

import ish.common.types.FieldConfigurationType
import ish.common.types.SurveyVisibility
import ish.common.types.TypesUtil
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.FieldConfiguration
import ish.oncourse.server.cayenne.Survey
import ish.oncourse.webservices.v22.stubs.replication.SurveyStub
import org.apache.cayenne.query.ObjectSelect

class SurveyUpdater extends AbstractAngelUpdater<SurveyStub,Survey> {

    @Override
    protected void updateEntity(SurveyStub stub, Survey entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
        entity.setComment(stub.getComment())
        entity.setTestimonial(stub.getTestimonial())
        entity.setCourseScore(stub.getCourseScore())
        entity.setEnrolment(callback.updateRelationShip(stub.getEnrolmentId(), Enrolment.class))
        entity.setVenueScore(stub.getVenueScore())
        entity.setTutorScore(stub.getTutorScore())
        entity.setNetPromoterScore(stub.getNetPromoterScore())
		entity.setVisibility(TypesUtil.getEnumForDatabaseValue(stub.getVisibility(), SurveyVisibility.class))
		if (stub.getFieldConfigurationId() != null) {
            entity.setFieldConfiguration(callback.updateRelationShip(stub.getFieldConfigurationId(), FieldConfiguration.class))
        } else {
            def surveyConfiguration = ObjectSelect.query(FieldConfiguration.class)
                    .where(FieldConfiguration.INT_TYPE.eq(FieldConfigurationType.SURVEY.getDatabaseValue()))
                    .selectFirst(entity.getObjectContext())
            entity.setFieldConfiguration(surveyConfiguration)
        }
    }
}
