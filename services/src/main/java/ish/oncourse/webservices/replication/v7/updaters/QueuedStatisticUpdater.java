package ish.oncourse.webservices.replication.v7.updaters;

import ish.oncourse.model.QueuedStatistic;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v7.stubs.replication.QueuedStatisticStub;

public class QueuedStatisticUpdater extends AbstractWillowUpdater<QueuedStatisticStub, QueuedStatistic> {

	@Override
	protected void updateEntity(final QueuedStatisticStub stub, final QueuedStatistic entity, final RelationShipCallback callback) {
		entity.setEntityIdentifier(stub.getStackedEntityIdentifier());
		entity.setReceivedTimestamp(stub.getReceivedTimestamp());
		entity.setStackedCount(stub.getStackedCount().intValue());
		entity.setStackedTransactionsCount(stub.getStackedTransactionsCount().intValue());
	}

}
