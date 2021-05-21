/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.Assessment
import ish.oncourse.webservices.v23.stubs.replication.AssessmentStub

/**
 * Created by Artem on 23/10/2016.
 */
class AssessmentStubBuilder extends AbstractAngelStubBuilder<Assessment, AssessmentStub> {

    @Override
    protected AssessmentStub createFullStub(Assessment entity) {
        def stub = new AssessmentStub()
        stub.setCreated(entity.getCreatedOn())
        stub.setModified(entity.getModifiedOn())
        stub.setCode(entity.getCode())
        stub.setName(entity.getName())
        stub.setIsActive(entity.getActive())
        stub.setDescription(entity.getDescription())
        stub.setGradingTypeId(entity.getGradingType()?.getId())
        return stub
    }
}
