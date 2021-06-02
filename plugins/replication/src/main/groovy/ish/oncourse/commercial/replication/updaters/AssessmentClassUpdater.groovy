/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.updaters

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.Assessment
import ish.oncourse.server.cayenne.AssessmentClass
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.webservices.v23.stubs.replication.AssessmentClassStub

@CompileStatic
class AssessmentClassUpdater extends AbstractAngelUpdater<AssessmentClassStub, AssessmentClass>{

    @Override
    protected void updateEntity(AssessmentClassStub stub, AssessmentClass entity, RelationShipCallback callback) {
        entity.setCreatedOn(stub.getCreated())
        entity.setModifiedOn(stub.getModified())
        entity.setReleaseDate(stub.getReleaseDate())
        entity.setDueDate(stub.getDueDate())
        entity.setAssessment(callback.updateRelationShip(stub.getAssessmentId(), Assessment.class))
        entity.setCourseClass(callback.updateRelationShip(stub.getCourseClassId(), CourseClass.class))
    }
}
