package ish.oncourse.webservices.util;

public abstract class GenericUnreplicatedEntitiesStub {
	public abstract void setId(Long value);
	public abstract Long getId();
	public abstract void setMessage(String value);
	public abstract String getMessage();
	public abstract void changeParameters(GenericParametersMap value);
}
