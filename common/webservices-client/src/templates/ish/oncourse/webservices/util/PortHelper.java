package ish.oncourse.webservices.util;

import java.util.ArrayList;
import java.util.List;

public class PortHelper {
<% replicationVersions.each { v -> %>
	public static ish.oncourse.webservices.v${v}.stubs.replication.ReplicationRecords getV${v}ReplicationRecords(final GenericReplicationRecords replicationRecords) {
		return (ish.oncourse.webservices.v${v}.stubs.replication.ReplicationRecords) replicationRecords;
	}
<% } %>
<% replicationVersions.each { v -> %>
	public static ish.oncourse.webservices.v${v}.stubs.replication.TransactionGroup getV${v}TransactionGroup(final GenericTransactionGroup transactionGroup) {
		return (ish.oncourse.webservices.v${v}.stubs.replication.TransactionGroup) transactionGroup;
	}
<% } %>
<% replicationVersions.each { v -> %>
	public static ish.oncourse.webservices.v${v}.stubs.replication.ReplicationResult getV${v}ReplicationResult(final GenericReplicationResult replicationResult) {
		return (ish.oncourse.webservices.v${v}.stubs.replication.ReplicationResult) replicationResult;
	}
<% } %>
	public static GenericReplicationResult createReplicationResult(final GenericReplicationRecords replicationRecords) {
<% replicationVersions.each { v -> %>
		if (replicationRecords instanceof ish.oncourse.webservices.v${v}.stubs.replication.ReplicationRecords) {
			return new ish.oncourse.webservices.v${v}.stubs.replication.ReplicationResult();
		}
<% } %>
		return null;
	}

	public static GenericTransactionGroup createTransactionGroup(final SupportedVersions version) {
		switch (version) {
<% replicationVersions.each { v -> %>
			case V${v}:
				return new ish.oncourse.webservices.v${v}.stubs.replication.TransactionGroup();
<% } %>
			default:
				return null;
		}
	}

	public static SupportedVersions getVersionByTransactionGroup(final GenericTransactionGroup group) {
<% replicationVersions.each { v -> %>
		if (group instanceof ish.oncourse.webservices.v${v}.stubs.replication.TransactionGroup) {
			return SupportedVersions.V${v};
		}
<% } %>
		return null;
	}

	public static SupportedVersions getVersionByReplicationStub(final GenericReplicationStub stub) {
<% replicationVersions.each { v -> %> 
		if (stub instanceof ish.oncourse.webservices.v${v}.stubs.replication.ReplicationStub) {
			return SupportedVersions.V${v};
		}
<% } %>
		return null;
	}

	public static SupportedVersions getVersionByInstructionStub(final GenericInstructionStub stub) {
<% replicationVersions.each { v -> %>
		if (stub instanceof ish.oncourse.webservices.v${v}.stubs.replication.InstructionStub) {
			return SupportedVersions.V${v};
		}
<% } %>
		throw new IllegalArgumentException("This version of InstructionStub is not supported.");
	}

	public static SupportedVersions getVersionByReplicatedRecord(final GenericReplicatedRecord record) {
<% replicationVersions.each { v -> %>
		if (record instanceof ish.oncourse.webservices.v${v}.stubs.replication.ReplicatedRecord) {
			return SupportedVersions.V${v};
		}
<% } %>
		throw new IllegalArgumentException("This version of ReplicatedRecord is not supported.");
	}

	public static SupportedVersions getVersionByParametersMap(GenericParametersMap parametersMap) {
<% replicationVersions.each { v -> %>
		if (parametersMap instanceof ish.oncourse.webservices.v${v}.stubs.replication.ParametersMap) {
			return SupportedVersions.V${v};
		}
<% } %>
		throw new IllegalArgumentException("This version of ParametersMap is not supported");
	}

	public static GenericTransactionGroup createTransactionGroup(final GenericTransactionGroup group) {
		return createTransactionGroup(getVersionByTransactionGroup(group));
	}

	public static GenericReplicationStub createDeleteStub(final SupportedVersions version) {
		switch (version) {
<% replicationVersions.each { v -> %>
			case V${v}:
				return new ish.oncourse.webservices.v${v}.stubs.replication.DeletedStub();
<% } %>
			default:
				return null;
		}
	}

	public static GenericReplicationRecords createReplicationRecords(final SupportedVersions version) {
		switch (version) {
<% replicationVersions.each { v -> %>
			case V${v}:
				return new ish.oncourse.webservices.v${v}.stubs.replication.ReplicationRecords();
<% } %>
			default:
				return null;
		}
	}

	public static GenericReplicationResult createReplicationResult(final SupportedVersions version) {
		switch (version) {
<% replicationVersions.each { v -> %>
			case V${v}:
				return new ish.oncourse.webservices.v${v}.stubs.replication.ReplicationResult();
<% } %>
			default:
				return null;
		}
	}

	public static GenericInstructionStub createInstructionStub(final SupportedVersions version) {
		switch (version) {
<% replicationVersions.each { v -> %>
			case V${v}:
				return new ish.oncourse.webservices.v${v}.stubs.replication.InstructionStub();
<% } %>
			default:
				return null;
		}
	}

	public static GenericParametersMap createParametersMap(final SupportedVersions version) {
		switch (version) {
<% replicationVersions.each { v -> %>
			case V${v}:
				return new ish.oncourse.webservices.v${v}.stubs.replication.ParametersMap();
<% } %>
			default:
				return null;
		}
	}

	public static GenericParameterEntry createParameterEntry(final SupportedVersions version) {
		switch (version) {
<% replicationVersions.each { v -> %>
			case V${v}:
				return new ish.oncourse.webservices.v${v}.stubs.replication.ParameterEntry();
<% } %>
			default:
				return null;
		}
	}
<% replicationVersions.each { v -> %>
	public static List<ish.oncourse.webservices.v${v}.stubs.replication.InstructionStub> convertV${v}InstructionsList(final List<GenericInstructionStub> list) {
		final List<ish.oncourse.webservices.v${v}.stubs.replication.InstructionStub> result = new ArrayList<>(list.size());
		for (GenericInstructionStub stub : list) {
			if (stub instanceof ish.oncourse.webservices.v${v}.stubs.replication.InstructionStub) {
				result.add((ish.oncourse.webservices.v${v}.stubs.replication.InstructionStub) stub);
			} else {
				throw new IllegalArgumentException("Incorrect convertV6InstructionsList usage");
			}
		}
		return result;
	}
<% } %>
}
