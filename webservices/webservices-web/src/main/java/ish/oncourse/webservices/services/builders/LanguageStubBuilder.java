/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.services.builders;

import org.apache.cayenne.Persistent;

import ish.oncourse.model.Language;
import ish.oncourse.webservices.v4.stubs.reference.LanguageStub;

/**
 * 
 * @author marek
 */
public final class LanguageStubBuilder implements IStubBuilder {

	public LanguageStub convert(Persistent p) {

		if (!(p instanceof Language)) {
			throw new IllegalArgumentException();
		}
		
		Language record = (Language) p;

		LanguageStub stub = new LanguageStub();

		stub.setWillowId((Long) record.readProperty(Language.ID_PK_COLUMN));
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
