package ish.oncourse.webservices.util;

import java.util.List;

public abstract class GenericTransactionGroup {
	public abstract List<String> getTransactionKeys();

	/**
	 * @deprecated - {@link ish.oncourse.webservices.util.GenericTransactionGroup#getReplicationStub()} should be
	 * 				 used instead, this method should be removed when v4 and v5 stubs will retire.
	 */
	@Deprecated
	public List<? extends GenericReplicationStub> getAttendanceOrBinaryDataOrBinaryInfo() {
		return getReplicationStub();
	}
	
	public abstract List<? extends GenericReplicationStub> getReplicationStub();
	
	@SuppressWarnings("unchecked")
	public final List<GenericReplicationStub> getGenericAttendanceOrBinaryDataOrBinaryInfo() {
		return (List<GenericReplicationStub>) getReplicationStub();
	}
}
