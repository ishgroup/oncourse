package ish.oncourse.webservices.util;

import java.util.List;

public abstract class GenericParametersMap {
	public abstract List<? extends GenericParameterEntry> getEntry();
	
	@SuppressWarnings("unchecked")
	public final List<GenericParameterEntry> getGenericEntry() {
		return (List<GenericParameterEntry>) getEntry();
	}
}
