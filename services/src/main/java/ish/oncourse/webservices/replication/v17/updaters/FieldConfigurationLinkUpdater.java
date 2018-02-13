package ish.oncourse.webservices.replication.v17.updaters;

import ish.common.types.FieldConfigurationType;
import ish.common.types.TypesUtil;
import ish.oncourse.model.*;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v17.stubs.replication.FieldConfigurationLinkStub;

public class FieldConfigurationLinkUpdater extends AbstractWillowUpdater<FieldConfigurationLinkStub, FieldConfigurationLink> {
    
    @Override
    protected void updateEntity(FieldConfigurationLinkStub stub, FieldConfigurationLink entity, RelationShipCallback callback) {
        entity.setCreated(stub.getCreated());
        entity.setModified(stub.getModified());
        entity.setFieldConfigurationScheme(callback.updateRelationShip(stub.getSchemeId(), FieldConfigurationScheme.class));

        Class<? extends FieldConfiguration> fcClass = null;
        
        FieldConfigurationType type = TypesUtil.getEnumForDatabaseValue(stub.getConfigurationType(), FieldConfigurationType.class);
        
        switch (type) {
            case APPLICATION:
                fcClass = ApplicationFieldConfiguration.class;
                break;
            case ENROLMENT:
                fcClass = EnrolmentFieldConfiguration.class;
                break;
            case WAITING_LIST:
                fcClass = WaitingListFieldConfiguration.class;
                break;
            case SURVEY:
                fcClass = SurveyFieldConfiguration.class;
                break;
        }

        entity.setFieldConfiguration(callback.updateRelationShip(stub.getConfigurationId(), fcClass));
    }
}
