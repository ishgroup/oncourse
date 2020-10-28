package ish.oncourse.webservices.replication.v22.updaters;


import ish.oncourse.model.FieldConfiguration;
import ish.oncourse.model.FieldConfigurationLink;
import ish.oncourse.model.FieldConfigurationScheme;
import ish.oncourse.utils.FieldConfigurationUtil;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v22.stubs.replication.FieldConfigurationLinkStub;

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
