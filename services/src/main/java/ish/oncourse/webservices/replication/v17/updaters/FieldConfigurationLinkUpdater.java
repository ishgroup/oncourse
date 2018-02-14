package ish.oncourse.webservices.replication.v17.updaters;


import ish.oncourse.model.*;
import ish.oncourse.utils.FieldConfigurationUtil;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v17.stubs.replication.FieldConfigurationLinkStub;

public class FieldConfigurationLinkUpdater extends AbstractWillowUpdater<FieldConfigurationLinkStub, FieldConfigurationLink> {
    
    @Override
    protected void updateEntity(FieldConfigurationLinkStub stub, FieldConfigurationLink entity, RelationShipCallback callback) {
        entity.setCreated(stub.getCreated());
        entity.setModified(stub.getModified());
        entity.setFieldConfigurationScheme(callback.updateRelationShip(stub.getSchemeId(), FieldConfigurationScheme.class));

        Class<? extends FieldConfiguration> fcClass = FieldConfigurationUtil.getClassByType(stub.getConfigurationType());
        entity.setFieldConfiguration(callback.updateRelationShip(stub.getConfigurationId(), fcClass));
    }
}
