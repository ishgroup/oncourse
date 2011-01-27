/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.soap.v4.builders;

import ish.oncourse.model.Language;
import ish.oncourse.webservices.v4.stubs.reference.LanguageStub;

/**
 *
 * @author marek
 */
public class LanguageStubBuilder {

	public static LanguageStub convert(Language record) {

		LanguageStub stub = new LanguageStub();

		stub.setWillowId((Long) record.readProperty(Language.ID_PK_COLUMN));
		stub.setAbsCode(record.getAbsCode());
		stub.setCreated(record.getCreated());
		Boolean isActive = (record.getIsActive() == null) ? null
				: record.getIsActive().intValue() == 1;
		stub.setIsActive(isActive);
		stub.setIshVersion(record.getIshVersion());
		stub.setModified(record.getModified());
		stub.setName(record.getName());

		return stub;
	}
}
