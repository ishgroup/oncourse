package ish.oncourse.webservices.replication.services;

import java.util.ArrayList;
import java.util.List;

import ish.oncourse.webservices.util.GenericInstructionStub;
import ish.oncourse.webservices.util.GenericParameterEntry;
import ish.oncourse.webservices.util.GenericParametersMap;
import ish.oncourse.webservices.util.GenericReplicationRecords;
import ish.oncourse.webservices.util.GenericReplicationResult;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;

public class PortHelper {
	
	public static ish.oncourse.webservices.v4.stubs.replication.ReplicationRecords getV4ReplicationRecords(final GenericReplicationRecords replicationRecords) {
		return (ish.oncourse.webservices.v4.stubs.replication.ReplicationRecords) replicationRecords;
	}

	public static ish.oncourse.webservices.v5.stubs.replication.ReplicationRecords getV5ReplicationRecords(final GenericReplicationRecords replicationRecords) {
		return (ish.oncourse.webservices.v5.stubs.replication.ReplicationRecords) replicationRecords;
	}

	public static ish.oncourse.webservices.v6.stubs.replication.ReplicationRecords getV6ReplicationRecords(final GenericReplicationRecords replicationRecords) {
		return (ish.oncourse.webservices.v6.stubs.replication.ReplicationRecords) replicationRecords;
	}

	public static ish.oncourse.webservices.v4.stubs.replication.TransactionGroup getV4TransactionGroup(final GenericTransactionGroup transactionGroup) {
		return (ish.oncourse.webservices.v4.stubs.replication.TransactionGroup) transactionGroup;
	}

	public static ish.oncourse.webservices.v5.stubs.replication.TransactionGroup getV5TransactionGroup(final GenericTransactionGroup transactionGroup) {
		return (ish.oncourse.webservices.v5.stubs.replication.TransactionGroup) transactionGroup;
	}

	public static ish.oncourse.webservices.v6.stubs.replication.TransactionGroup getV6TransactionGroup(final GenericTransactionGroup transactionGroup) {
		return (ish.oncourse.webservices.v6.stubs.replication.TransactionGroup) transactionGroup;
	}

	public static ish.oncourse.webservices.v4.stubs.replication.ReplicationResult getV4ReplicationResult(final GenericReplicationResult replicationResult) {
		return (ish.oncourse.webservices.v4.stubs.replication.ReplicationResult) replicationResult;
	}

	public static ish.oncourse.webservices.v5.stubs.replication.ReplicationResult getV5ReplicationResult(final GenericReplicationResult replicationResult) {
		return (ish.oncourse.webservices.v5.stubs.replication.ReplicationResult) replicationResult;
	}

	public static ish.oncourse.webservices.v6.stubs.replication.ReplicationResult getV6ReplicationResult(final GenericReplicationResult replicationResult) {
		return (ish.oncourse.webservices.v6.stubs.replication.ReplicationResult) replicationResult;
	}

	public static GenericReplicationResult createReplicationResult(final GenericReplicationRecords replicationRecords) {
		if (replicationRecords instanceof ish.oncourse.webservices.v4.stubs.replication.ReplicationRecords) {
			return new ish.oncourse.webservices.v4.stubs.replication.ReplicationResult();
		} else if (replicationRecords instanceof ish.oncourse.webservices.v5.stubs.replication.ReplicationRecords) {
			return new ish.oncourse.webservices.v5.stubs.replication.ReplicationResult();
		} else if (replicationRecords instanceof ish.oncourse.webservices.v6.stubs.replication.ReplicationRecords) {
			return new ish.oncourse.webservices.v6.stubs.replication.ReplicationResult();
		}
		return null;
	}

	public static GenericTransactionGroup createTransactionGroup(final SupportedVersions version) {
		switch (version) {
			case V4:
				return new ish.oncourse.webservices.v4.stubs.replication.TransactionGroup();
			case V5:
				return new ish.oncourse.webservices.v5.stubs.replication.TransactionGroup();
			case V6:
				return new ish.oncourse.webservices.v6.stubs.replication.TransactionGroup();
			default:
				return null;
		}
	}

	public static SupportedVersions getVersionByTransactionGroup(final GenericTransactionGroup group) {
		if (group instanceof ish.oncourse.webservices.v4.stubs.replication.TransactionGroup) {
			return SupportedVersions.V4;
		} else if (group instanceof ish.oncourse.webservices.v5.stubs.replication.TransactionGroup) {
			return SupportedVersions.V5;
		} else if (group instanceof ish.oncourse.webservices.v6.stubs.replication.TransactionGroup) {
			return SupportedVersions.V6;
		}
		return null;
	}
	
	public static SupportedVersions getVersionByReplicationStub(final GenericReplicationStub stub) {
		if (stub instanceof ish.oncourse.webservices.v4.stubs.replication.ReplicationStub) {
			return SupportedVersions.V4;
		} else if (stub instanceof ish.oncourse.webservices.v5.stubs.replication.ReplicationStub) {
			return SupportedVersions.V5;
		} else if (stub instanceof ish.oncourse.webservices.v6.stubs.replication.ReplicationStub) {
			return SupportedVersions.V6;
		}
		return null;
	}

	public static GenericTransactionGroup createTransactionGroup(final GenericTransactionGroup group) {
		return createTransactionGroup(getVersionByTransactionGroup(group));
	}
	
	public static GenericReplicationStub createDeleteStub(final SupportedVersions version) {
		switch (version) {
		case V4:
			return new ish.oncourse.webservices.v4.stubs.replication.DeletedStub();
		case V5:
			return new ish.oncourse.webservices.v5.stubs.replication.DeletedStub();
		case V6:
			return new ish.oncourse.webservices.v6.stubs.replication.DeletedStub();
		default:
			return null;
		}
	}

	public static GenericReplicationRecords createReplicationRecords(final SupportedVersions version) {
		switch (version) {
			case V4:
				return new ish.oncourse.webservices.v4.stubs.replication.ReplicationRecords();
			case V5:
				return new ish.oncourse.webservices.v5.stubs.replication.ReplicationRecords();
			case V6:
				return new ish.oncourse.webservices.v6.stubs.replication.ReplicationRecords();
			default:
				return null;
		}
	}
	
	public static GenericReplicationResult createReplicationResult(final SupportedVersions version) {
		switch (version) {
			case V4:
				return new ish.oncourse.webservices.v4.stubs.replication.ReplicationResult();
			case V5:
				return new ish.oncourse.webservices.v5.stubs.replication.ReplicationResult();
			case V6:
				return new ish.oncourse.webservices.v6.stubs.replication.ReplicationResult();
			default:
				return null;
		}
	}

	public static GenericInstructionStub createInstructionStub(final SupportedVersions version) {
		switch (version) {
			case V4:
				return new ish.oncourse.webservices.v4.stubs.replication.InstructionStub();
			case V5:
				return new ish.oncourse.webservices.v5.stubs.replication.InstructionStub();
			case V6:
				return new ish.oncourse.webservices.v6.stubs.replication.InstructionStub();
			default:
				return null;
		}
	}

	public static GenericParametersMap createParametersMap(final SupportedVersions version) {
		switch (version) {
			case V4:
				return new ish.oncourse.webservices.v4.stubs.replication.ParametersMap();
			case V5:
				return new ish.oncourse.webservices.v5.stubs.replication.ParametersMap();
			case V6:
				return new ish.oncourse.webservices.v6.stubs.replication.ParametersMap();
			default:
				return null;
		}
	}

	public static GenericParameterEntry createParameterEntry(final SupportedVersions version) {
		switch (version) {
			case V4:
				return new ish.oncourse.webservices.v4.stubs.replication.ParameterEntry();
			case V5:
				return new ish.oncourse.webservices.v5.stubs.replication.ParameterEntry();
			case V6:
				return new ish.oncourse.webservices.v6.stubs.replication.ParameterEntry();
			default:
				return null;
		}
	}

	public static List<ish.oncourse.webservices.v4.stubs.replication.InstructionStub> convertV4InstructionsList(final List<GenericInstructionStub> list) {
		final List<ish.oncourse.webservices.v4.stubs.replication.InstructionStub> result = new ArrayList<>(list.size());
		for (GenericInstructionStub stub : list) {
			if (stub instanceof ish.oncourse.webservices.v4.stubs.replication.InstructionStub) {
				result.add((ish.oncourse.webservices.v4.stubs.replication.InstructionStub) stub);
			} else {
				throw new IllegalArgumentException("Incorrect convertV4InstructionsList usage");
			}
		}
		return result;
	}

	public static List<ish.oncourse.webservices.v5.stubs.replication.InstructionStub> convertV5InstructionsList(final List<GenericInstructionStub> list) {
		final List<ish.oncourse.webservices.v5.stubs.replication.InstructionStub> result = new ArrayList<>(list.size());
		for (GenericInstructionStub stub : list) {
			if (stub instanceof ish.oncourse.webservices.v5.stubs.replication.InstructionStub) {
				result.add((ish.oncourse.webservices.v5.stubs.replication.InstructionStub) stub);
			} else {
				throw new IllegalArgumentException("Incorrect convertV5InstructionsList usage");
			}
		}
		return result;
	}

	public static List<ish.oncourse.webservices.v6.stubs.replication.InstructionStub> convertV6InstructionsList(final List<GenericInstructionStub> list) {
		final List<ish.oncourse.webservices.v6.stubs.replication.InstructionStub> result = new ArrayList<>(list.size());
		for (GenericInstructionStub stub : list) {
			if (stub instanceof ish.oncourse.webservices.v6.stubs.replication.InstructionStub) {
				result.add((ish.oncourse.webservices.v6.stubs.replication.InstructionStub) stub);
			} else {
				throw new IllegalArgumentException("Incorrect convertV6InstructionsList usage");
			}
		}
		return result;
	}

}
