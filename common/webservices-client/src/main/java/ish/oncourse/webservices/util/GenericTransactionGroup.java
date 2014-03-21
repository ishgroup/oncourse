package ish.oncourse.webservices.util;

import java.util.List;

public abstract class GenericTransactionGroup {
	public abstract List<String> getTransactionKeys();
	
	public abstract List<? extends GenericReplicationStub> getAttendanceOrBinaryDataOrBinaryInfo();
	
	@SuppressWarnings("unchecked")
	public final List<GenericReplicationStub> getGenericAttendanceOrBinaryDataOrBinaryInfo() {
		return (List<GenericReplicationStub>) getAttendanceOrBinaryDataOrBinaryInfo();
	}
}
