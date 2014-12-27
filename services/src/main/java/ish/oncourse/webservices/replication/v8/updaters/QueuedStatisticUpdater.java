package ish.oncourse.webservices.replication.v8.updaters;

import ish.oncourse.model.QueuedStatistic;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v8.stubs.replication.QueuedStatisticStub;

public class QueuedStatisticUpdater extends AbstractWillowUpdater<QueuedStatisticStub, QueuedStatistic> {

	@Override
	protected void updateEntity(final QueuedStatisticStub stub, final QueuedStatistic entity, final RelationShipCallback callback) {
		entity.setEntityIdentifier(stub.getStackedEntityIdentifier());
		entity.setReceivedTimestamp(stub.getReceivedTimestamp());
		entity.setStackedCount(stub.getStackedCount().intValue());
		entity.setStackedTransactionsCount(stub.getStackedTransactionsCount().intValue());
	}

}
