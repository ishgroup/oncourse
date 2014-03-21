package ish.oncourse.webservices.util;

import java.util.Date;

public abstract class GenericReplicationStub {

	public abstract String getEntityIdentifier();
	public abstract Long getAngelId();
	public abstract void setAngelId(Long value);
	public abstract Long getWillowId();
	public abstract void setWillowId(Long value);
	public abstract void setEntityIdentifier(String value);
	public abstract void setCreated(Date value);
	public abstract void setModified(Date value);
	
	@Override
	public boolean equals(Object arg0) {

		if (arg0 instanceof GenericReplicationStub) {

			GenericReplicationStub stub = (GenericReplicationStub) arg0;

			if (!getEntityIdentifier().equals(stub.getEntityIdentifier())) {
				return false;
			}

			if (getAngelId() != null) {
				if (!getAngelId().equals(stub.getAngelId())) {
					return false;
				}
			}

			if (getWillowId() != null) {
				if (!getWillowId().equals(stub.getWillowId())) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		String str = String.format("%s-%s-%s", getEntityIdentifier(), getAngelId(), getWillowId());
		return str.hashCode();
	}
}
