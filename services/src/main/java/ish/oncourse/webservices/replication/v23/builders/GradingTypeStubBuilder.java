package ish.oncourse.webservices.replication.v23.builders;

import ish.oncourse.model.GradingType;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v23.stubs.replication.GradingTypeStub;

public class GradingTypeStubBuilder extends AbstractWillowStubBuilder<GradingType, GradingTypeStub> {
    @Override
    protected GradingTypeStub createFullStub(GradingType entity) {
        GradingTypeStub stub = new GradingTypeStub();
        stub.setModified(entity.getModified());
        stub.setName(entity.getTypeName());
        stub.setMinValue(entity.getMinValue());
        stub.setMaxValue(entity.getMaxValue());
        stub.setEntryType(entity.getEntryType().getDatabaseValue());
        return stub;
    }
}
