package ish.oncourse.webservices.util;

import java.util.List;
public abstract class GenericReplicationRecords {
	public abstract List<? extends GenericTransactionGroup> getGroups();
	
	@SuppressWarnings("unchecked")
	public final List<GenericTransactionGroup> getGenericGroups() {
		return (List<GenericTransactionGroup>) getGroups();
	}
}
