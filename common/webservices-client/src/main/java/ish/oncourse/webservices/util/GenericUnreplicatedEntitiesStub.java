package ish.oncourse.webservices.util;

public abstract class GenericUnreplicatedEntitiesStub {
	public abstract void setId(Long value);
	public abstract Long getId();
	public abstract void setMessage(String value);
	public abstract String getMessage();

	/**
	 * @deprecated - this method is obsolete and should not be called from anywhere in the code
	 * 				 it should be removed when v4 and v5 stubs will retire.
	 */
	@Deprecated
	public void changeParameters(GenericParametersMap value) {
		throw new UnsupportedOperationException("This method is obsolete and should not be called.");
	}
}
