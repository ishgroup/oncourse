package ish.oncourse.webservices.replication.v22.builders;

import ish.oncourse.model.FieldConfigurationLink;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v22.stubs.replication.FieldConfigurationLinkStub;

public class FieldConfigurationLinkStubBuilder extends AbstractWillowStubBuilder<FieldConfigurationLink, FieldConfigurationLinkStub> {
    
    @Override
    protected FieldConfigurationLinkStub createFullStub(FieldConfigurationLink entity) {
        FieldConfigurationLinkStub stub = new FieldConfigurationLinkStub();
        stub.setCreated(entity.getCreated());
        stub.setModified(entity.getModified());
        stub.setSchemeId(entity.getFieldConfigurationScheme().getId());
        stub.setConfigurationId(entity.getFieldConfiguration().getId());
        stub.setConfigurationType(entity.getFieldConfiguration().getType().getDatabaseValue());
        return stub;
    }
}
