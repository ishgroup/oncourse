package ish.oncourse.server.replication.updaters

import ish.oncourse.server.cayenne.Assessment
import ish.oncourse.webservices.v21.stubs.replication.AssessmentStub

/**
 * Created by Artem on 24/10/2016.
 */
class AssessmentUpdater extends AbstractAngelUpdater<AssessmentStub, Assessment> {
    @Override
    protected void updateEntity(AssessmentStub stub, Assessment entity, RelationShipCallback callback) {
        entity.setCreatedOn(stub.getCreated())
        entity.setModifiedOn(stub.getModified())
        entity.setActive(stub.isIsActive())
        entity.setCode(stub.getCode())
        entity.setName(stub.getName())
        entity.setDescription(stub.getDescription())
    }
}
