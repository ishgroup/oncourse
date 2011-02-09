/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.builders.reference;

import org.apache.cayenne.Persistent;

import ish.oncourse.model.Language;
import ish.oncourse.webservices.builders.IReferenceStubBuilder;
import ish.oncourse.webservices.v4.stubs.reference.LanguageStub;

/**
 * 
 * @author marek
 */
public final class LanguageStubBuilder implements IReferenceStubBuilder {

	public LanguageStub convert(Persistent entity) {
		
		Language record = (Language) entity;
		
		LanguageStub stub = new LanguageStub();

		stub.setWillowId(record.getId());
		stub.setAbsCode(record.getAbsCode());
		stub.setCreated(record.getCreated());
		Boolean isActive = (record.getIsActive() == null) ? null : record.getIsActive().intValue() == 1;
		stub.setIsActive(isActive);
		stub.setIshVersion(record.getIshVersion());
		stub.setModified(record.getModified());
		stub.setName(record.getName());

		return stub;
	}
}
