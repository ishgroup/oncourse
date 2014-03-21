package ish.oncourse.webservices.util;

import java.util.List;

public abstract class GenericReferenceResult {
	
	public abstract List<? extends GenericReferenceStub> getCountryOrLanguageOrModule();
	
	@SuppressWarnings("unchecked")
	public List<GenericReferenceStub> getGenericCountryOrLanguageOrModule() {
		return (List<GenericReferenceStub>) getCountryOrLanguageOrModule();
	}

}
