package ish.oncourse.webservices.util;

import java.util.ArrayList;
import java.util.List;

public class PortHelper {

	public static ish.oncourse.webservices.v10.stubs.replication.ReplicationRecords getV10ReplicationRecords(final GenericReplicationRecords replicationRecords) {
		return (ish.oncourse.webservices.v10.stubs.replication.ReplicationRecords) replicationRecords;
	}

	public static ish.oncourse.webservices.v14.stubs.replication.ReplicationRecords getV14ReplicationRecords(final GenericReplicationRecords replicationRecords) {
		return (ish.oncourse.webservices.v14.stubs.replication.ReplicationRecords) replicationRecords;
	}

	public static ish.oncourse.webservices.v15.stubs.replication.ReplicationRecords getV15ReplicationRecords(final GenericReplicationRecords replicationRecords) {
		return (ish.oncourse.webservices.v15.stubs.replication.ReplicationRecords) replicationRecords;
	}

	public static ish.oncourse.webservices.v16.stubs.replication.ReplicationRecords getV16ReplicationRecords(final GenericReplicationRecords replicationRecords) {
		return (ish.oncourse.webservices.v16.stubs.replication.ReplicationRecords) replicationRecords;
	}


	public static ish.oncourse.webservices.v10.stubs.replication.TransactionGroup getV10TransactionGroup(final GenericTransactionGroup transactionGroup) {
		return (ish.oncourse.webservices.v10.stubs.replication.TransactionGroup) transactionGroup;
	}

	public static ish.oncourse.webservices.v14.stubs.replication.TransactionGroup getV14TransactionGroup(final GenericTransactionGroup transactionGroup) {
		return (ish.oncourse.webservices.v14.stubs.replication.TransactionGroup) transactionGroup;
	}

	public static ish.oncourse.webservices.v15.stubs.replication.TransactionGroup getV15TransactionGroup(final GenericTransactionGroup transactionGroup) {
		return (ish.oncourse.webservices.v15.stubs.replication.TransactionGroup) transactionGroup;
	}

	public static ish.oncourse.webservices.v16.stubs.replication.TransactionGroup getV16TransactionGroup(final GenericTransactionGroup transactionGroup) {
		return (ish.oncourse.webservices.v16.stubs.replication.TransactionGroup) transactionGroup;
	}


	public static ish.oncourse.webservices.v10.stubs.replication.ReplicationResult getV10ReplicationResult(final GenericReplicationResult replicationResult) {
		return (ish.oncourse.webservices.v10.stubs.replication.ReplicationResult) replicationResult;
	}

	public static ish.oncourse.webservices.v14.stubs.replication.ReplicationResult getV14ReplicationResult(final GenericReplicationResult replicationResult) {
		return (ish.oncourse.webservices.v14.stubs.replication.ReplicationResult) replicationResult;
	}

	public static ish.oncourse.webservices.v15.stubs.replication.ReplicationResult getV15ReplicationResult(final GenericReplicationResult replicationResult) {
		return (ish.oncourse.webservices.v15.stubs.replication.ReplicationResult) replicationResult;
	}

	public static ish.oncourse.webservices.v16.stubs.replication.ReplicationResult getV16ReplicationResult(final GenericReplicationResult replicationResult) {
		return (ish.oncourse.webservices.v16.stubs.replication.ReplicationResult) replicationResult;
	}

	public static GenericReplicationResult createReplicationResult(final GenericReplicationRecords replicationRecords) {

		if (replicationRecords instanceof ish.oncourse.webservices.v10.stubs.replication.ReplicationRecords) {
			return new ish.oncourse.webservices.v10.stubs.replication.ReplicationResult();
		}

		if (replicationRecords instanceof ish.oncourse.webservices.v14.stubs.replication.ReplicationRecords) {
			return new ish.oncourse.webservices.v14.stubs.replication.ReplicationResult();
		}

		if (replicationRecords instanceof ish.oncourse.webservices.v15.stubs.replication.ReplicationRecords) {
			return new ish.oncourse.webservices.v15.stubs.replication.ReplicationResult();
		}

		if (replicationRecords instanceof ish.oncourse.webservices.v16.stubs.replication.ReplicationRecords) {
			return new ish.oncourse.webservices.v16.stubs.replication.ReplicationResult();
		}

		return null;
	}

	public static GenericTransactionGroup createTransactionGroup(final SupportedVersions version) {
		switch (version) {

			case V10:
				return new ish.oncourse.webservices.v10.stubs.replication.TransactionGroup();

			case V14:
				return new ish.oncourse.webservices.v14.stubs.replication.TransactionGroup();

			case V15:
				return new ish.oncourse.webservices.v15.stubs.replication.TransactionGroup();

			case V16:
				return new ish.oncourse.webservices.v16.stubs.replication.TransactionGroup();

			default:
				return null;
		}
	}

	public static SupportedVersions getVersionByTransactionGroup(final GenericTransactionGroup group) {

		if (group instanceof ish.oncourse.webservices.v10.stubs.replication.TransactionGroup) {
			return SupportedVersions.V10;
		}

		if (group instanceof ish.oncourse.webservices.v14.stubs.replication.TransactionGroup) {
			return SupportedVersions.V14;
		}

		if (group instanceof ish.oncourse.webservices.v15.stubs.replication.TransactionGroup) {
			return SupportedVersions.V15;
		}

		if (group instanceof ish.oncourse.webservices.v16.stubs.replication.TransactionGroup) {
			return SupportedVersions.V16;
		}

		return null;
	}

	public static SupportedVersions getVersionByReplicationStub(final GenericReplicationStub stub) {
 
		if (stub instanceof ish.oncourse.webservices.v10.stubs.replication.ReplicationStub) {
			return SupportedVersions.V10;
		}
 
		if (stub instanceof ish.oncourse.webservices.v14.stubs.replication.ReplicationStub) {
			return SupportedVersions.V14;
		}
 
		if (stub instanceof ish.oncourse.webservices.v15.stubs.replication.ReplicationStub) {
			return SupportedVersions.V15;
		}
 
		if (stub instanceof ish.oncourse.webservices.v16.stubs.replication.ReplicationStub) {
			return SupportedVersions.V16;
		}

		return null;
	}

	public static SupportedVersions getVersionByInstructionStub(final GenericInstructionStub stub) {

		if (stub instanceof ish.oncourse.webservices.v10.stubs.replication.InstructionStub) {
			return SupportedVersions.V10;
		}

		if (stub instanceof ish.oncourse.webservices.v14.stubs.replication.InstructionStub) {
			return SupportedVersions.V14;
		}

		if (stub instanceof ish.oncourse.webservices.v15.stubs.replication.InstructionStub) {
			return SupportedVersions.V15;
		}

		if (stub instanceof ish.oncourse.webservices.v16.stubs.replication.InstructionStub) {
			return SupportedVersions.V16;
		}

		throw new IllegalArgumentException("This version of InstructionStub is not supported.");
	}

	public static SupportedVersions getVersionByReplicatedRecord(final GenericReplicatedRecord record) {

		if (record instanceof ish.oncourse.webservices.v10.stubs.replication.ReplicatedRecord) {
			return SupportedVersions.V10;
		}

		if (record instanceof ish.oncourse.webservices.v14.stubs.replication.ReplicatedRecord) {
			return SupportedVersions.V14;
		}

		if (record instanceof ish.oncourse.webservices.v15.stubs.replication.ReplicatedRecord) {
			return SupportedVersions.V15;
		}

		if (record instanceof ish.oncourse.webservices.v16.stubs.replication.ReplicatedRecord) {
			return SupportedVersions.V16;
		}

		throw new IllegalArgumentException("This version of ReplicatedRecord is not supported.");
	}

	public static SupportedVersions getVersionByParametersMap(GenericParametersMap parametersMap) {

		if (parametersMap instanceof ish.oncourse.webservices.v10.stubs.replication.ParametersMap) {
			return SupportedVersions.V10;
		}

		if (parametersMap instanceof ish.oncourse.webservices.v14.stubs.replication.ParametersMap) {
			return SupportedVersions.V14;
		}

		if (parametersMap instanceof ish.oncourse.webservices.v15.stubs.replication.ParametersMap) {
			return SupportedVersions.V15;
		}

		if (parametersMap instanceof ish.oncourse.webservices.v16.stubs.replication.ParametersMap) {
			return SupportedVersions.V16;
		}

		throw new IllegalArgumentException("This version of ParametersMap is not supported");
	}

	public static GenericTransactionGroup createTransactionGroup(final GenericTransactionGroup group) {
		return createTransactionGroup(getVersionByTransactionGroup(group));
	}

	public static GenericReplicationStub createDeleteStub(final SupportedVersions version) {
		switch (version) {

			case V10:
				return new ish.oncourse.webservices.v10.stubs.replication.DeletedStub();

			case V14:
				return new ish.oncourse.webservices.v14.stubs.replication.DeletedStub();

			case V15:
				return new ish.oncourse.webservices.v15.stubs.replication.DeletedStub();

			case V16:
				return new ish.oncourse.webservices.v16.stubs.replication.DeletedStub();

			default:
				return null;
		}
	}

	public static GenericReplicationRecords createReplicationRecords(final SupportedVersions version) {
		switch (version) {

			case V10:
				return new ish.oncourse.webservices.v10.stubs.replication.ReplicationRecords();

			case V14:
				return new ish.oncourse.webservices.v14.stubs.replication.ReplicationRecords();

			case V15:
				return new ish.oncourse.webservices.v15.stubs.replication.ReplicationRecords();

			case V16:
				return new ish.oncourse.webservices.v16.stubs.replication.ReplicationRecords();

			default:
				return null;
		}
	}

	public static GenericReplicationResult createReplicationResult(final SupportedVersions version) {
		switch (version) {

			case V10:
				return new ish.oncourse.webservices.v10.stubs.replication.ReplicationResult();

			case V14:
				return new ish.oncourse.webservices.v14.stubs.replication.ReplicationResult();

			case V15:
				return new ish.oncourse.webservices.v15.stubs.replication.ReplicationResult();

			case V16:
				return new ish.oncourse.webservices.v16.stubs.replication.ReplicationResult();

			default:
				return null;
		}
	}

	public static GenericInstructionStub createInstructionStub(final SupportedVersions version) {
		switch (version) {

			case V10:
				return new ish.oncourse.webservices.v10.stubs.replication.InstructionStub();

			case V14:
				return new ish.oncourse.webservices.v14.stubs.replication.InstructionStub();

			case V15:
				return new ish.oncourse.webservices.v15.stubs.replication.InstructionStub();

			case V16:
				return new ish.oncourse.webservices.v16.stubs.replication.InstructionStub();

			default:
				return null;
		}
	}

	public static GenericParametersMap createParametersMap(final SupportedVersions version) {
		switch (version) {

			case V10:
				return new ish.oncourse.webservices.v10.stubs.replication.ParametersMap();

			case V14:
				return new ish.oncourse.webservices.v14.stubs.replication.ParametersMap();

			case V15:
				return new ish.oncourse.webservices.v15.stubs.replication.ParametersMap();

			case V16:
				return new ish.oncourse.webservices.v16.stubs.replication.ParametersMap();

			default:
				return null;
		}
	}

	public static GenericParameterEntry createParameterEntry(final SupportedVersions version) {
		switch (version) {

			case V10:
				return new ish.oncourse.webservices.v10.stubs.replication.ParameterEntry();

			case V14:
				return new ish.oncourse.webservices.v14.stubs.replication.ParameterEntry();

			case V15:
				return new ish.oncourse.webservices.v15.stubs.replication.ParameterEntry();

			case V16:
				return new ish.oncourse.webservices.v16.stubs.replication.ParameterEntry();

			default:
				return null;
		}
	}

	public static List<ish.oncourse.webservices.v10.stubs.replication.InstructionStub> convertV10InstructionsList(final List<GenericInstructionStub> list) {
		final List<ish.oncourse.webservices.v10.stubs.replication.InstructionStub> result = new ArrayList<>(list.size());
		for (GenericInstructionStub stub : list) {
			if (stub instanceof ish.oncourse.webservices.v10.stubs.replication.InstructionStub) {
				result.add((ish.oncourse.webservices.v10.stubs.replication.InstructionStub) stub);
			} else {
				throw new IllegalArgumentException("Incorrect convertV6InstructionsList usage");
			}
		}
		return result;
	}

	public static List<ish.oncourse.webservices.v14.stubs.replication.InstructionStub> convertV14InstructionsList(final List<GenericInstructionStub> list) {
		final List<ish.oncourse.webservices.v14.stubs.replication.InstructionStub> result = new ArrayList<>(list.size());
		for (GenericInstructionStub stub : list) {
			if (stub instanceof ish.oncourse.webservices.v14.stubs.replication.InstructionStub) {
				result.add((ish.oncourse.webservices.v14.stubs.replication.InstructionStub) stub);
			} else {
				throw new IllegalArgumentException("Incorrect convertV6InstructionsList usage");
			}
		}
		return result;
	}

	public static List<ish.oncourse.webservices.v15.stubs.replication.InstructionStub> convertV15InstructionsList(final List<GenericInstructionStub> list) {
		final List<ish.oncourse.webservices.v15.stubs.replication.InstructionStub> result = new ArrayList<>(list.size());
		for (GenericInstructionStub stub : list) {
			if (stub instanceof ish.oncourse.webservices.v15.stubs.replication.InstructionStub) {
				result.add((ish.oncourse.webservices.v15.stubs.replication.InstructionStub) stub);
			} else {
				throw new IllegalArgumentException("Incorrect convertV6InstructionsList usage");
			}
		}
		return result;
	}

	public static List<ish.oncourse.webservices.v16.stubs.replication.InstructionStub> convertV16InstructionsList(final List<GenericInstructionStub> list) {
		final List<ish.oncourse.webservices.v16.stubs.replication.InstructionStub> result = new ArrayList<>(list.size());
		for (GenericInstructionStub stub : list) {
			if (stub instanceof ish.oncourse.webservices.v16.stubs.replication.InstructionStub) {
				result.add((ish.oncourse.webservices.v16.stubs.replication.InstructionStub) stub);
			} else {
				throw new IllegalArgumentException("Incorrect convertV6InstructionsList usage");
			}
		}
		return result;
	}

}
