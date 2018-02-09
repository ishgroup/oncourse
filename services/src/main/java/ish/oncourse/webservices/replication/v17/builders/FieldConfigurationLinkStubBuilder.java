package ish.oncourse.webservices.replication.v17.builders;

import ish.oncourse.model.FieldConfigurationLink;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v17.stubs.replication.FieldConfigurationLinkStub;

public class FieldConfigurationLinkStubBuilder extends AbstractWillowStubBuilder<FieldConfigurationLink, FieldConfigurationLinkStub> {
    
    @Override
    protected FieldConfigurationLinkStub createFullStub(FieldConfigurationLink entity) {
        FieldConfigurationLinkStub stub = new FieldConfigurationLinkStub();
        stub.setCreated(entity.getCreated());
        stub.setModified(entity.getModified());
        stub.setSchemeId(entity.getFieldConfigurationScheme().getId());
        stub.setConfigurationId(entity.getFieldConfiguration().getId());
        return stub;
    }
}
