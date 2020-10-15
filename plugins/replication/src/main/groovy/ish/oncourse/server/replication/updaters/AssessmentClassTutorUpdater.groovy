package ish.oncourse.server.replication.updaters

import ish.oncourse.server.cayenne.AssessmentClass
import ish.oncourse.server.cayenne.AssessmentClassTutor
import ish.oncourse.server.cayenne.Tutor
import ish.oncourse.webservices.v21.stubs.replication.AssessmentClassTutorStub

/**
 * Created by Artem on 24/10/2016.
 */
class AssessmentClassTutorUpdater extends AbstractAngelUpdater<AssessmentClassTutorStub, AssessmentClassTutor> {

    @Override
    protected void updateEntity(AssessmentClassTutorStub stub, AssessmentClassTutor entity, RelationShipCallback callback) {
        entity.setCreatedOn(stub.getCreated())
        entity.setModifiedOn(stub.getModified())
        entity.setAssessmentClass(callback.updateRelationShip(stub.getAssessmentClassId(), AssessmentClass.class))
        entity.setTutor(callback.updateRelationShip(stub.getTutorId(), Tutor.class))
    }
}
