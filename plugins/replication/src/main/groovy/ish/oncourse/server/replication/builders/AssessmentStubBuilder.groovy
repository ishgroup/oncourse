package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.Assessment
import ish.oncourse.webservices.v21.stubs.replication.AssessmentStub

/**
 * Created by Artem on 22/10/2016.
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
        return stub
    }
}
