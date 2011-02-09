package ish.oncourse.webservices.services.replication;

import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.builders.IReplicationStubBuilder;
import ish.oncourse.webservices.builders.replication.BinaryDataStubBuilder;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

import java.util.HashMap;
import java.util.Map;

public class ReplicationStubBuilderFactory {

	private static class ReplicationStubBuilderImpl implements IReplicationStubBuilder {

		private Map<String, IReplicationStubBuilder> builderMap = new HashMap<String, IReplicationStubBuilder>();

		public ReplicationStubBuilderImpl(Map<QueuedKey, QueuedRecord> queue) {
			builderMap.put("BinaryData", new BinaryDataStubBuilder(queue, this));
		}

		@Override
		public ReplicationStub convert(QueuedRecord entity) {
			return builderMap.get(entity.getObjectId().getEntityName()).convert(entity);
		}
	}

	public IReplicationStubBuilder newReplicationStubBuilder(Map<QueuedKey, QueuedRecord> queue) {
		return new ReplicationStubBuilderImpl(queue);
	}
}
