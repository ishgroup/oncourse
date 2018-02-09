package ish.oncourse.webservices.replication.v17.updaters;

import ish.oncourse.model.FieldConfigurationLink;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v17.stubs.replication.FieldConfigurationLinkStub;

public class FieldConfigurationLinkUpdater extends AbstractWillowUpdater<FieldConfigurationLinkStub, FieldConfigurationLink> {
    
    @Override
    protected void updateEntity(FieldConfigurationLinkStub stub, FieldConfigurationLink entity, RelationShipCallback callback) {
        entity.setCreated(stub.getCreated());
        entity.setModified(stub.getModified());
        entity.setFieldConfigurationScheme(entity.getFieldConfigurationScheme());
        entity.setFieldConfiguration(entity.getFieldConfiguration());
    }
}
