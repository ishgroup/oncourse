package ish.oncourse.webservices.util;

import java.util.List;

public abstract class GenericReplicationResult {
	public abstract List<? extends GenericReplicatedRecord> getReplicatedRecord();
	
	@SuppressWarnings("unchecked")
	public final List<GenericReplicatedRecord> getGenericReplicatedRecord() {
		return (List<GenericReplicatedRecord>) getReplicatedRecord();
	}
}
