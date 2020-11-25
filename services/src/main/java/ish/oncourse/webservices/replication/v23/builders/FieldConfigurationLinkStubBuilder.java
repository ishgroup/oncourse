package ish.oncourse.webservices.replication.v23.builders;

import ish.oncourse.model.FieldConfigurationLink;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v23.stubs.replication.FieldConfigurationLinkStub;

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
