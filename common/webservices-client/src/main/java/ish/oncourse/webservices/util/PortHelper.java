package ish.oncourse.webservices.util;

import java.util.ArrayList;
import java.util.List;

public class PortHelper {
	
	public static ish.oncourse.webservices.v6.stubs.replication.ReplicationRecords getV6ReplicationRecords(final GenericReplicationRecords replicationRecords) {
		return (ish.oncourse.webservices.v6.stubs.replication.ReplicationRecords) replicationRecords;
	}

	public static ish.oncourse.webservices.v7.stubs.replication.ReplicationRecords getV7ReplicationRecords(final GenericReplicationRecords replicationRecords) {
		return (ish.oncourse.webservices.v7.stubs.replication.ReplicationRecords) replicationRecords;
	}

	public static ish.oncourse.webservices.v8.stubs.replication.ReplicationRecords getV8ReplicationRecords(final GenericReplicationRecords replicationRecords) {
		return (ish.oncourse.webservices.v8.stubs.replication.ReplicationRecords) replicationRecords;
	}

	public static ish.oncourse.webservices.v9.stubs.replication.ReplicationRecords getV9ReplicationRecords(final GenericReplicationRecords replicationRecords) {
		return (ish.oncourse.webservices.v9.stubs.replication.ReplicationRecords) replicationRecords;
	}

	public static ish.oncourse.webservices.v6.stubs.replication.TransactionGroup getV6TransactionGroup(final GenericTransactionGroup transactionGroup) {
		return (ish.oncourse.webservices.v6.stubs.replication.TransactionGroup) transactionGroup;
	}

	public static ish.oncourse.webservices.v7.stubs.replication.TransactionGroup getV7TransactionGroup(final GenericTransactionGroup transactionGroup) {
		return (ish.oncourse.webservices.v7.stubs.replication.TransactionGroup) transactionGroup;
	}

	public static ish.oncourse.webservices.v8.stubs.replication.TransactionGroup getV8TransactionGroup(final GenericTransactionGroup transactionGroup) {
		return (ish.oncourse.webservices.v8.stubs.replication.TransactionGroup) transactionGroup;
	}

	public static ish.oncourse.webservices.v9.stubs.replication.TransactionGroup getV9TransactionGroup(final GenericTransactionGroup transactionGroup) {
		return (ish.oncourse.webservices.v9.stubs.replication.TransactionGroup) transactionGroup;
	}


	public static ish.oncourse.webservices.v6.stubs.replication.ReplicationResult getV6ReplicationResult(final GenericReplicationResult replicationResult) {
		return (ish.oncourse.webservices.v6.stubs.replication.ReplicationResult) replicationResult;
	}

	public static ish.oncourse.webservices.v7.stubs.replication.ReplicationResult getV7ReplicationResult(final GenericReplicationResult replicationResult) {
		return (ish.oncourse.webservices.v7.stubs.replication.ReplicationResult) replicationResult;
	}

	public static ish.oncourse.webservices.v8.stubs.replication.ReplicationResult getV8ReplicationResult(final GenericReplicationResult replicationResult) {
		return (ish.oncourse.webservices.v8.stubs.replication.ReplicationResult) replicationResult;
	}

	public static ish.oncourse.webservices.v9.stubs.replication.ReplicationResult getV9ReplicationResult(final GenericReplicationResult replicationResult) {
		return (ish.oncourse.webservices.v9.stubs.replication.ReplicationResult) replicationResult;
	}
	
	public static GenericReplicationResult createReplicationResult(final GenericReplicationRecords replicationRecords) {
		if (replicationRecords instanceof ish.oncourse.webservices.v6.stubs.replication.ReplicationRecords) {
			return new ish.oncourse.webservices.v6.stubs.replication.ReplicationResult();
		} else if (replicationRecords instanceof ish.oncourse.webservices.v7.stubs.replication.ReplicationRecords) {
			return new ish.oncourse.webservices.v7.stubs.replication.ReplicationResult();
		} else if (replicationRecords instanceof ish.oncourse.webservices.v8.stubs.replication.ReplicationRecords) {
			return new ish.oncourse.webservices.v8.stubs.replication.ReplicationResult();
		} else if (replicationRecords instanceof ish.oncourse.webservices.v9.stubs.replication.ReplicationRecords) {
			return new ish.oncourse.webservices.v9.stubs.replication.ReplicationResult();
		}
		return null;
	}

	public static GenericTransactionGroup createTransactionGroup(final SupportedVersions version) {
		switch (version) {
			case V6:
				return new ish.oncourse.webservices.v6.stubs.replication.TransactionGroup();
			case V7:
				return new ish.oncourse.webservices.v7.stubs.replication.TransactionGroup();
			case V8:
				return new ish.oncourse.webservices.v8.stubs.replication.TransactionGroup();
			case V9:
				return new ish.oncourse.webservices.v9.stubs.replication.TransactionGroup();
			default:
				return null;
		}
	}

	public static SupportedVersions getVersionByTransactionGroup(final GenericTransactionGroup group) {
		if (group instanceof ish.oncourse.webservices.v6.stubs.replication.TransactionGroup) {
			return SupportedVersions.V6;
		} else if (group instanceof ish.oncourse.webservices.v7.stubs.replication.TransactionGroup) {
			return SupportedVersions.V7;
		} else if (group instanceof ish.oncourse.webservices.v8.stubs.replication.TransactionGroup) {
			return SupportedVersions.V8;
		} else if (group instanceof ish.oncourse.webservices.v9.stubs.replication.TransactionGroup) {
			return SupportedVersions.V9;
		}
		return null;
	}
	
	public static SupportedVersions getVersionByReplicationStub(final GenericReplicationStub stub) {
		if (stub instanceof ish.oncourse.webservices.v6.stubs.replication.ReplicationStub) {
			return SupportedVersions.V6;
		} else if (stub instanceof ish.oncourse.webservices.v7.stubs.replication.ReplicationStub) {
			return SupportedVersions.V7;
		} else if (stub instanceof ish.oncourse.webservices.v8.stubs.replication.ReplicationStub) {
			return SupportedVersions.V8;
		} else if (stub instanceof ish.oncourse.webservices.v9.stubs.replication.ReplicationStub) {
			return SupportedVersions.V9;
		}
		return null;
	}
	
	public static SupportedVersions getVersionByInstructionStub(final GenericInstructionStub stub) {
		if (stub instanceof ish.oncourse.webservices.v6.stubs.replication.InstructionStub) {
			return SupportedVersions.V6;
		} else if (stub instanceof ish.oncourse.webservices.v7.stubs.replication.InstructionStub) {
			return SupportedVersions.V7;
		} else if (stub instanceof ish.oncourse.webservices.v8.stubs.replication.InstructionStub) {
			return SupportedVersions.V8;
		} else if (stub instanceof ish.oncourse.webservices.v9.stubs.replication.InstructionStub) {
			return SupportedVersions.V9;
		}
		
		throw new IllegalArgumentException("This version of InstructionStub is not supported.");
	}
	
	public static SupportedVersions getVersionByReplicatedRecord(final GenericReplicatedRecord record) {
		if (record instanceof ish.oncourse.webservices.v6.stubs.replication.ReplicatedRecord) {
			return SupportedVersions.V6;
		} else if (record instanceof ish.oncourse.webservices.v7.stubs.replication.ReplicatedRecord) {
			return SupportedVersions.V7;
		} else if (record instanceof ish.oncourse.webservices.v8.stubs.replication.ReplicatedRecord) {
			return SupportedVersions.V8;
		} else if (record instanceof ish.oncourse.webservices.v9.stubs.replication.ReplicatedRecord) {
			return SupportedVersions.V9;
		}
		
		throw new IllegalArgumentException("This version of ReplicatedRecord is not supported.");
	}
	
	public static SupportedVersions getVersionByParametersMap(GenericParametersMap parametersMap) {
		if (parametersMap instanceof ish.oncourse.webservices.v6.stubs.replication.ParametersMap) {
			return SupportedVersions.V6;
		} else if (parametersMap instanceof ish.oncourse.webservices.v7.stubs.replication.ParametersMap) {
			return SupportedVersions.V7;
		} else if (parametersMap instanceof ish.oncourse.webservices.v8.stubs.replication.ParametersMap) {
			return SupportedVersions.V8;
		} else if (parametersMap instanceof ish.oncourse.webservices.v9.stubs.replication.ParametersMap) {
			return SupportedVersions.V9;
		}
		
		throw new IllegalArgumentException("This version of ParametersMap is not supported");
	}

	public static GenericTransactionGroup createTransactionGroup(final GenericTransactionGroup group) {
		return createTransactionGroup(getVersionByTransactionGroup(group));
	}
	
	public static GenericReplicationStub createDeleteStub(final SupportedVersions version) {
		switch (version) {
		case V6:
			return new ish.oncourse.webservices.v6.stubs.replication.DeletedStub();
		case V7:
			return new ish.oncourse.webservices.v7.stubs.replication.DeletedStub();
		case V8:
			return new ish.oncourse.webservices.v8.stubs.replication.DeletedStub();
		case V9:
			return new ish.oncourse.webservices.v9.stubs.replication.DeletedStub();
		default:
			return null;
		}
	}

	public static GenericReplicationRecords createReplicationRecords(final SupportedVersions version) {
		switch (version) {
			case V6:
				return new ish.oncourse.webservices.v6.stubs.replication.ReplicationRecords();
			case V7:
				return new ish.oncourse.webservices.v7.stubs.replication.ReplicationRecords();
			case V8:
				return new ish.oncourse.webservices.v8.stubs.replication.ReplicationRecords();
			case V9:
				return new ish.oncourse.webservices.v9.stubs.replication.ReplicationRecords();
			default:
				return null;
		}
	}
	
	public static GenericReplicationResult createReplicationResult(final SupportedVersions version) {
		switch (version) {
			case V6:
				return new ish.oncourse.webservices.v6.stubs.replication.ReplicationResult();
			case V7:
				return new ish.oncourse.webservices.v7.stubs.replication.ReplicationResult();
			case V8:
				return new ish.oncourse.webservices.v8.stubs.replication.ReplicationResult();
			case V9:
				return new ish.oncourse.webservices.v9.stubs.replication.ReplicationResult();
			default:
				return null;
		}
	}

	public static GenericInstructionStub createInstructionStub(final SupportedVersions version) {
		switch (version) {
			case V6:
				return new ish.oncourse.webservices.v6.stubs.replication.InstructionStub();
			case V7:
				return new ish.oncourse.webservices.v7.stubs.replication.InstructionStub();
			case V8:
				return new ish.oncourse.webservices.v8.stubs.replication.InstructionStub();
			case V9:
				return new ish.oncourse.webservices.v9.stubs.replication.InstructionStub();
			default:
				return null;
		}
	}

	public static GenericParametersMap createParametersMap(final SupportedVersions version) {
		switch (version) {
			case V6:
				return new ish.oncourse.webservices.v6.stubs.replication.ParametersMap();
			case V7:
				return new ish.oncourse.webservices.v7.stubs.replication.ParametersMap();
			case V8:
				return new ish.oncourse.webservices.v8.stubs.replication.ParametersMap();
			case V9:
				return new ish.oncourse.webservices.v9.stubs.replication.ParametersMap();
			default:
				return null;
		}
	}

	public static GenericParameterEntry createParameterEntry(final SupportedVersions version) {
		switch (version) {
			case V6:
				return new ish.oncourse.webservices.v6.stubs.replication.ParameterEntry();
			case V7:
				return new ish.oncourse.webservices.v7.stubs.replication.ParameterEntry();
			case V8:
				return new ish.oncourse.webservices.v8.stubs.replication.ParameterEntry();
			case V9:
				return new ish.oncourse.webservices.v9.stubs.replication.ParameterEntry();
			default:
				return null;
		}
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

	public static List<ish.oncourse.webservices.v7.stubs.replication.InstructionStub> convertV7InstructionsList(final List<GenericInstructionStub> list) {
		final List<ish.oncourse.webservices.v7.stubs.replication.InstructionStub> result = new ArrayList<>(list.size());
		for (GenericInstructionStub stub : list) {
			if (stub instanceof ish.oncourse.webservices.v7.stubs.replication.InstructionStub) {
				result.add((ish.oncourse.webservices.v7.stubs.replication.InstructionStub) stub);
			} else {
				throw new IllegalArgumentException("Incorrect convertV6InstructionsList usage");
			}
		}
		return result;
	}

	public static List<ish.oncourse.webservices.v8.stubs.replication.InstructionStub> convertV8InstructionsList(final List<GenericInstructionStub> list) {
		final List<ish.oncourse.webservices.v8.stubs.replication.InstructionStub> result = new ArrayList<>(list.size());
		for (GenericInstructionStub stub : list) {
			if (stub instanceof ish.oncourse.webservices.v8.stubs.replication.InstructionStub) {
				result.add((ish.oncourse.webservices.v8.stubs.replication.InstructionStub) stub);
			} else {
				throw new IllegalArgumentException("Incorrect convertV6InstructionsList usage");
			}
		}
		return result;
	}
	
	public static List<ish.oncourse.webservices.v9.stubs.replication.InstructionStub> convertV9InstructionsList(final List<GenericInstructionStub> list) {
		final List<ish.oncourse.webservices.v9.stubs.replication.InstructionStub> result = new ArrayList<>(list.size());
		for (GenericInstructionStub stub : list) {
			if (stub instanceof ish.oncourse.webservices.v9.stubs.replication.InstructionStub) {
				result.add((ish.oncourse.webservices.v9.stubs.replication.InstructionStub) stub);
			} else {
				throw new IllegalArgumentException("Incorrect convertV6InstructionsList usage");
			}
		}
		return result;
	}
}
