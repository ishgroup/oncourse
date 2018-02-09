package ish.oncourse.webservices.replication.v17.updaters;

import ish.common.types.SurveyVisibility;
import ish.common.types.TypesUtil;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Survey;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v17.stubs.replication.SurveyStub;

public class SurveyUpdater extends AbstractWillowUpdater<SurveyStub, Survey> {
    @Override
    protected void updateEntity(SurveyStub stub, Survey entity, RelationShipCallback callback) {
        entity.setModified(stub.getModified());
        entity.setCreated(stub.getCreated());
        entity.setComment(stub.getComment());
        entity.setTestimonial(stub.getTestimonial());
        entity.setCourseScore(stub.getCourseScore());
        entity.setTutorScore(stub.getTutorScore());
        entity.setVenueScore(stub.getVenueScore());
        entity.setNetPromoterScore(stub.getNetPromoterScore());
        entity.setEnrolment(callback.updateRelationShip(stub.getEnrolmentId(), Enrolment.class));
        entity.setVisibility(TypesUtil.getEnumForDatabaseValue(stub.getVisibility(), SurveyVisibility.class));
	}
}
