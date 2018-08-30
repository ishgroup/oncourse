package ish.oncourse.webservices.util;

import java.util.ArrayList;
import java.util.List;

public class PortHelper {

	public static ish.oncourse.webservices.v13.stubs.replication.ReplicationRecords getV13ReplicationRecords(final GenericReplicationRecords replicationRecords) {
		return (ish.oncourse.webservices.v13.stubs.replication.ReplicationRecords) replicationRecords;
	}

	public static ish.oncourse.webservices.v17.stubs.replication.ReplicationRecords getV17ReplicationRecords(final GenericReplicationRecords replicationRecords) {
		return (ish.oncourse.webservices.v17.stubs.replication.ReplicationRecords) replicationRecords;
	}

	public static ish.oncourse.webservices.v13.stubs.replication.TransactionGroup getV13TransactionGroup(final GenericTransactionGroup transactionGroup) {
		return (ish.oncourse.webservices.v13.stubs.replication.TransactionGroup) transactionGroup;
	}

	public static ish.oncourse.webservices.v17.stubs.replication.TransactionGroup getV17TransactionGroup(final GenericTransactionGroup transactionGroup) {
		return (ish.oncourse.webservices.v17.stubs.replication.TransactionGroup) transactionGroup;
	}

	public static ish.oncourse.webservices.v13.stubs.replication.ReplicationResult getV13ReplicationResult(final GenericReplicationResult replicationResult) {
		return (ish.oncourse.webservices.v13.stubs.replication.ReplicationResult) replicationResult;
	}

	public static ish.oncourse.webservices.v17.stubs.replication.ReplicationResult getV17ReplicationResult(final GenericReplicationResult replicationResult) {
		return (ish.oncourse.webservices.v17.stubs.replication.ReplicationResult) replicationResult;
	}

	public static GenericReplicationResult createReplicationResult(final GenericReplicationRecords replicationRecords) {

		if (replicationRecords instanceof ish.oncourse.webservices.v13.stubs.replication.ReplicationRecords) {
			return new ish.oncourse.webservices.v13.stubs.replication.ReplicationResult();
		}

		if (replicationRecords instanceof ish.oncourse.webservices.v17.stubs.replication.ReplicationRecords) {
			return new ish.oncourse.webservices.v17.stubs.replication.ReplicationResult();
		}

		return null;
	}

	public static GenericTransactionGroup createTransactionGroup(final SupportedVersions version) {
		switch (version) {

			case V13:
				return new ish.oncourse.webservices.v13.stubs.replication.TransactionGroup();

			case V17:
				return new ish.oncourse.webservices.v17.stubs.replication.TransactionGroup();
				
			default:
				return null;
		}
	}

	public static SupportedVersions getVersionByTransactionGroup(final GenericTransactionGroup group) {

		if (group instanceof ish.oncourse.webservices.v13.stubs.replication.TransactionGroup) {
			return SupportedVersions.V13;
		}

		if (group instanceof ish.oncourse.webservices.v17.stubs.replication.TransactionGroup) {
			return SupportedVersions.V17;
		}

		return null;
	}

	public static SupportedVersions getVersionByReplicationStub(final GenericReplicationStub stub) {

		if (stub instanceof ish.oncourse.webservices.v13.stubs.replication.ReplicationStub) {
			return SupportedVersions.V13;
		}

		if (stub instanceof ish.oncourse.webservices.v17.stubs.replication.ReplicationStub) {
			return SupportedVersions.V17;
		}

		return null;
	}

	public static SupportedVersions getVersionByInstructionStub(final GenericInstructionStub stub) {

		if (stub instanceof ish.oncourse.webservices.v13.stubs.replication.InstructionStub) {
			return SupportedVersions.V13;
		}

		if (stub instanceof ish.oncourse.webservices.v17.stubs.replication.InstructionStub) {
			return SupportedVersions.V17;
		}
		
		throw new IllegalArgumentException("This version of InstructionStub is not supported.");
	}

	public static SupportedVersions getVersionByReplicatedRecord(final GenericReplicatedRecord record) {


		if (record instanceof ish.oncourse.webservices.v13.stubs.replication.ReplicatedRecord) {
			return SupportedVersions.V13;
		}

		if (record instanceof ish.oncourse.webservices.v17.stubs.replication.ReplicatedRecord) {
			return SupportedVersions.V17;
		}

		throw new IllegalArgumentException("This version of ReplicatedRecord is not supported.");
	}

	public static SupportedVersions getVersionByParametersMap(GenericParametersMap parametersMap) {

		if (parametersMap instanceof ish.oncourse.webservices.v13.stubs.replication.ParametersMap) {
			return SupportedVersions.V13;
		}

		if (parametersMap instanceof ish.oncourse.webservices.v17.stubs.replication.ParametersMap) {
			return SupportedVersions.V17;
		}

		throw new IllegalArgumentException("This version of ParametersMap is not supported");
	}

	public static GenericTransactionGroup createTransactionGroup(final GenericTransactionGroup group) {
		return createTransactionGroup(getVersionByTransactionGroup(group));
	}

	public static GenericReplicationStub createDeleteStub(final SupportedVersions version) {
		switch (version) {

			case V13:
				return new ish.oncourse.webservices.v13.stubs.replication.DeletedStub();

			case V17:
				return new ish.oncourse.webservices.v17.stubs.replication.DeletedStub();

			default:
				return null;
		}
	}

	public static GenericReplicationRecords createReplicationRecords(final SupportedVersions version) {
		switch (version) {

			case V13:
				return new ish.oncourse.webservices.v13.stubs.replication.ReplicationRecords();

			case V17:
				return new ish.oncourse.webservices.v17.stubs.replication.ReplicationRecords();
				
			default:
				return null;
		}
	}

	public static GenericReplicationResult createReplicationResult(final SupportedVersions version) {
		switch (version) {

			case V13:
				return new ish.oncourse.webservices.v13.stubs.replication.ReplicationResult();

			case V17:
				return new ish.oncourse.webservices.v17.stubs.replication.ReplicationResult();
				
			default:
				return null;
		}
	}

	public static GenericInstructionStub createInstructionStub(final SupportedVersions version) {
		switch (version) {

			case V13:
				return new ish.oncourse.webservices.v13.stubs.replication.InstructionStub();

			case V17:
				return new ish.oncourse.webservices.v17.stubs.replication.InstructionStub();
				
			default:
				return null;
		}
	}

	public static GenericParametersMap createParametersMap(final SupportedVersions version) {
		switch (version) {

			case V13:
				return new ish.oncourse.webservices.v13.stubs.replication.ParametersMap();

			case V17:
				return new ish.oncourse.webservices.v17.stubs.replication.ParametersMap();
				
			default:
				return null;
		}
	}

	public static GenericParameterEntry createParameterEntry(final SupportedVersions version) {
		switch (version) {

			case V13:
				return new ish.oncourse.webservices.v13.stubs.replication.ParameterEntry();

			case V17:
				return new ish.oncourse.webservices.v17.stubs.replication.ParameterEntry();
				
			default:
				return null;
		}
	}

	public static List<ish.oncourse.webservices.v13.stubs.replication.InstructionStub> convertV13InstructionsList(final List<GenericInstructionStub> list) {
		final List<ish.oncourse.webservices.v13.stubs.replication.InstructionStub> result = new ArrayList<>(list.size());
		for (GenericInstructionStub stub : list) {
			if (stub instanceof ish.oncourse.webservices.v13.stubs.replication.InstructionStub) {
				result.add((ish.oncourse.webservices.v13.stubs.replication.InstructionStub) stub);
			} else {
				throw new IllegalArgumentException("Incorrect convertV6InstructionsList usage");
			}
		}
		return result;
	}

	public static List<ish.oncourse.webservices.v17.stubs.replication.InstructionStub> convertV17InstructionsList(final List<GenericInstructionStub> list) {
		final List<ish.oncourse.webservices.v17.stubs.replication.InstructionStub> result = new ArrayList<>(list.size());
		for (GenericInstructionStub stub : list) {
			if (stub instanceof ish.oncourse.webservices.v17.stubs.replication.InstructionStub) {
				result.add((ish.oncourse.webservices.v17.stubs.replication.InstructionStub) stub);
			} else {
				throw new IllegalArgumentException("Incorrect convertV6InstructionsList usage");
			}
		}
		return result;
	}
}
