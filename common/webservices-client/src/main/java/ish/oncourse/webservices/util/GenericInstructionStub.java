package ish.oncourse.webservices.util;

public abstract class GenericInstructionStub {
	public abstract void setId(Long value);
	public abstract Long getId();
	public abstract void setMessage(String value);
	public abstract String getMessage();

	/**
	 * @deprecated - ReplicationUtils#setInstructionParameters() should be used instead, 
	 * 				 this method is not used since version 6 of replication stub and should be removed 
	 * 				 when v4 and v5 stubs will go away.
	 */
	@Deprecated
	public void changeParameters(GenericParametersMap value) {
		throw new UnsupportedOperationException("This method should be overridden by subclass.");
	}
}
