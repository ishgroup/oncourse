package ish.oncourse.webservices.util;

public abstract class GenericReplicatedRecord {
	
	public abstract String getMessage();
	public abstract void setMessage(String value);
	public abstract GenericReplicationStub getStub();

	/**
	 * @deprecated - {@link StubUtils#hasSuccessStatus(GenericReplicatedRecord)} should be used instead,
	 * 				 this method should be removed when v4 and v5 stubs will retire
	 */
	@Deprecated
	public boolean isSuccessStatus() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @deprecated - {@link StubUtils#hasFailedStatus(GenericReplicatedRecord)} should be used instead,
	 * 				 this method should be removed when v4 and v5 stubs will retire
	 */
	@Deprecated
	public boolean isFailedStatus() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @deprecated - {@link StubUtils#setSuccessStatus(GenericReplicatedRecord)} should be used instead,
	 * 				 this method should be removed when v4 and v5 stubs will retire
	 */
	@Deprecated
	public void setSuccessStatus() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @deprecated - {@link StubUtils#setFailedStatus(GenericReplicatedRecord)} should be used instead,
	 * 				 this method should be removed when v4 and v5 stubs will retire
	 */
	@Deprecated
	public void setFailedStatus() {
		throw new UnsupportedOperationException();
	}
}
