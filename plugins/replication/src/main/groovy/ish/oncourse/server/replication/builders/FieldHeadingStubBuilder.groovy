package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.FieldHeading
import ish.oncourse.webservices.v21.stubs.replication.FieldConfigurationStub
import ish.oncourse.webservices.v21.stubs.replication.FieldHeadingStub

/**
 * Created by Artem on 16/11/2016.
 */
class FieldHeadingStubBuilder extends AbstractAngelStubBuilder<FieldHeading, FieldHeadingStub> {

    FieldConfigurationStub stub = new FieldConfigurationStub()

    @Override
    protected FieldHeadingStub createFullStub(FieldHeading entity) {
        def stub = new FieldHeadingStub()
        stub.setCreated(entity.getCreatedOn())
        stub.setModified(entity.getModifiedOn())
        stub.setName(entity.getName())
        stub.setDescription(entity.getDescription())
        stub.setFieldConfigurationId(entity.getFieldConfiguration().getId())
        stub.setConfigurationType(entity.getFieldConfiguration().getType().getDatabaseValue())
        stub.setOrder(entity.getFieldOrder())
        return stub
    }


}
