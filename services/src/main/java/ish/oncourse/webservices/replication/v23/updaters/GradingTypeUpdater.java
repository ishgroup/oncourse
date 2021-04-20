package ish.oncourse.webservices.replication.v23.updaters;

import ish.common.types.GradingEntryType;
import ish.common.types.TypesUtil;
import ish.oncourse.model.GradingType;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v23.stubs.replication.GradingTypeStub;

public class GradingTypeUpdater extends AbstractWillowUpdater<GradingTypeStub, GradingType> {
    @Override
    protected void updateEntity(GradingTypeStub stub, GradingType entity, RelationShipCallback callback) {
        entity.setCreated(stub.getCreated());
        entity.setModified(stub.getModified());
        entity.setTypeName(stub.getName());
        entity.setMinValue(stub.getMinValue());
        entity.setMaxValue(stub.getMaxValue());
        entity.setEntryType(TypesUtil.getEnumForDatabaseValue(stub.getEntryType(), GradingEntryType.class));
    }
}
