package ish.oncourse.webservices.util;

public abstract class GenericReplicatedRecord {
	public abstract String getMessage();
	public abstract void setMessage(String value);
	public abstract GenericReplicationStub getStub();
	public abstract boolean isSuccessStatus();
	public abstract boolean isFailedStatus();
	public abstract void setSuccessStatus();
	public abstract void setFailedStatus();
	//public abstract void setStub(GenericReplicationStub value);
}
