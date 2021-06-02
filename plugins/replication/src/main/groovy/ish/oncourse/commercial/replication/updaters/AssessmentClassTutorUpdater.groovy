/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.updaters

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.AssessmentClass
import ish.oncourse.server.cayenne.AssessmentClassTutor
import ish.oncourse.server.cayenne.Tutor
import ish.oncourse.webservices.v23.stubs.replication.AssessmentClassTutorStub

@CompileStatic
class AssessmentClassTutorUpdater extends AbstractAngelUpdater<AssessmentClassTutorStub, AssessmentClassTutor> {

    @Override
    protected void updateEntity(AssessmentClassTutorStub stub, AssessmentClassTutor entity, RelationShipCallback callback) {
        entity.setCreatedOn(stub.getCreated())
        entity.setModifiedOn(stub.getModified())
        entity.setAssessmentClass(callback.updateRelationShip(stub.getAssessmentClassId(), AssessmentClass.class))
        entity.setTutor(callback.updateRelationShip(stub.getTutorId(), Tutor.class))
    }
}
