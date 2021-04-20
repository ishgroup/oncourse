package ish.oncourse.webservices.replication.v23.builders;

import ish.oncourse.model.GradingItem;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v23.stubs.replication.GradingItemStub;

public class GradingItemStubBuilder extends AbstractWillowStubBuilder<GradingItem, GradingItemStub> {
    @Override
    protected GradingItemStub createFullStub(GradingItem entity) {
        GradingItemStub stub = new GradingItemStub();
        stub.setModified(entity.getModified());
        stub.setName(entity.getItemName());
        stub.setLowerBound(entity.getLowerBound());
        stub.setGradingTypeId(entity.getGradingType().getId());
        return stub;
    }
}
