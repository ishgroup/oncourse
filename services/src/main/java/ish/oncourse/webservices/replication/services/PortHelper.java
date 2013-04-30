package ish.oncourse.webservices.replication.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ish.oncourse.webservices.util.GenericInstructionStub;
import ish.oncourse.webservices.util.GenericParameterEntry;
import ish.oncourse.webservices.util.GenericParametersMap;
import ish.oncourse.webservices.util.GenericReplicationRecords;
import ish.oncourse.webservices.util.GenericReplicationResult;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.v4.stubs.replication.DeletedStub;
import ish.oncourse.webservices.v4.stubs.replication.InstructionStub;
import ish.oncourse.webservices.v4.stubs.replication.ParameterEntry;
import ish.oncourse.webservices.v4.stubs.replication.ParametersMap;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationRecords;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationResult;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.TransactionGroup;

public class PortHelper {
	
	public static ReplicationRecords getv4ReplicationRecords(final GenericReplicationRecords replicationRecords) {
		return (ReplicationRecords) replicationRecords;
	}

	public static ish.oncourse.webservices.v5.stubs.replication.ReplicationRecords getv5ReplicationRecords(final GenericReplicationRecords replicationRecords) {
		return (ish.oncourse.webservices.v5.stubs.replication.ReplicationRecords) replicationRecords;
	}

	public static TransactionGroup getv4TransactionGroup(final GenericTransactionGroup transactionGroup) {
		return (TransactionGroup) transactionGroup;
	}

	public static ish.oncourse.webservices.v5.stubs.replication.TransactionGroup getv5TransactionGroup(final GenericTransactionGroup transactionGroup) {
		return (ish.oncourse.webservices.v5.stubs.replication.TransactionGroup) transactionGroup;
	}

	public static ReplicationResult getv4ReplicationResult(final GenericReplicationResult replicationResult) {
		return (ReplicationResult) replicationResult;
	}

	public static ish.oncourse.webservices.v5.stubs.replication.ReplicationResult getv5ReplicationResult(final GenericReplicationResult replicationResult) {
		return (ish.oncourse.webservices.v5.stubs.replication.ReplicationResult) replicationResult;
	}

	public static GenericReplicationResult createReplicationResult(final GenericReplicationRecords replicationRecords) {
		if (replicationRecords instanceof ReplicationRecords) {
			return new ReplicationResult();
		} else if (replicationRecords instanceof ish.oncourse.webservices.v5.stubs.replication.ReplicationRecords) {
			return new ish.oncourse.webservices.v5.stubs.replication.ReplicationResult();
		}
		return null;
	}

	public static GenericTransactionGroup createTransactionGroup(final SupportedVersions version) {
		switch (version) {
			case V4:
				return new TransactionGroup();
			case V5:
				return new ish.oncourse.webservices.v5.stubs.replication.TransactionGroup();
			default:
				return null;
		}
	}

	public static SupportedVersions getVersionByTransactionGroup(final GenericTransactionGroup group) {
		if (group instanceof TransactionGroup) {
			return SupportedVersions.V4;
		} else if (group instanceof ish.oncourse.webservices.v5.stubs.replication.TransactionGroup) {
			return SupportedVersions.V5;
		}
		return null;
	}
	
	public static SupportedVersions getVersionByReplicationStub(final GenericReplicationStub stub) {
		if (stub instanceof ReplicationStub) {
			return SupportedVersions.V4;
		} else if (stub instanceof ish.oncourse.webservices.v5.stubs.replication.ReplicationStub) {
			return SupportedVersions.V5;
		}
		return null;
	}

	public static GenericTransactionGroup createTransactionGroup(final GenericTransactionGroup group) {
		return createTransactionGroup(getVersionByTransactionGroup(group));
	}
	
	public static GenericReplicationStub createDeleteStub(final SupportedVersions version) {
		switch (version) {
		case V4:
			return new DeletedStub();
		case V5:
			return new ish.oncourse.webservices.v5.stubs.replication.DeletedStub();
		default:
			return null;
		}
	}
	
	public static void updateCreated(final GenericReplicationStub stub, final Date date) {
		final SupportedVersions version = getVersionByReplicationStub(stub);
		switch (version) {
			case V4:
				((ReplicationStub)stub).setCreated(date);
				break;
			case V5:
				((ish.oncourse.webservices.v5.stubs.replication.ReplicationStub)stub).setCreated(date);
				break;
			default:
				throw new IllegalStateException("Unsupported stub version used");
		}
	}

	public static GenericReplicationRecords createReplicationRecords(final SupportedVersions version) {
		switch (version) {
			case V4:
				return new ReplicationRecords();
			case V5:
				return new ish.oncourse.webservices.v5.stubs.replication.ReplicationRecords();
			default:
				return null;
		}
	}
	
	public static GenericReplicationResult createReplicationResult(final SupportedVersions version) {
		switch (version) {
			case V4:
				return new ReplicationResult();
			case V5:
				return new ish.oncourse.webservices.v5.stubs.replication.ReplicationResult();
			default:
				return null;
		}
	}

	public static GenericInstructionStub createInstructionStub(final SupportedVersions version) {
		switch (version) {
			case V4:
				return new InstructionStub();
			case V5:
				return new ish.oncourse.webservices.v5.stubs.replication.InstructionStub();
			default:
				return null;
		}
	}

	public static GenericParametersMap createParametersMap(final SupportedVersions version) {
		switch (version) {
			case V4:
				return new ParametersMap();
			case V5:
				return new ish.oncourse.webservices.v5.stubs.replication.ParametersMap();
			default:
				return null;
		}
	}

	public static GenericParameterEntry createParameterEntry(final SupportedVersions version) {
		switch (version) {
			case V4:
				return new ParameterEntry();
			case V5:
				return new ish.oncourse.webservices.v5.stubs.replication.ParameterEntry();
			default:
				return null;
		}
	}

	public static List<InstructionStub> convertv4InstructionsList(final List<GenericInstructionStub> list) {
		final List<InstructionStub> result = new ArrayList<>(list.size());
		for (GenericInstructionStub stub : list) {
			if (stub instanceof InstructionStub) {
				result.add((InstructionStub) stub);
			} else {
				throw new IllegalArgumentException("Incorrect convertv4InstructionsList usage");
			}
		}
		return result;
	}

	public static List<ish.oncourse.webservices.v5.stubs.replication.InstructionStub> convertv5InstructionsList(final List<GenericInstructionStub> list) {
		final List<ish.oncourse.webservices.v5.stubs.replication.InstructionStub> result = new ArrayList<>(
		list.size());
		for (GenericInstructionStub stub : list) {
			if (stub instanceof ish.oncourse.webservices.v5.stubs.replication.InstructionStub) {
				result.add((ish.oncourse.webservices.v5.stubs.replication.InstructionStub) stub);
			} else {
				throw new IllegalArgumentException("Incorrect convertv5InstructionsList usage");
			}
		}
		return result;
	}

}
