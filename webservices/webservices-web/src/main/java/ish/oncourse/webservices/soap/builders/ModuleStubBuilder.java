/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.soap.builders;

import ish.oncourse.model.Module;
import ish.oncourse.webservices.soap.stubs.Module_Stub;

/**
 *
 * @author marek
 */
public class ModuleStubBuilder {

	public static final Module_Stub convert(Module record) {

		Module_Stub stub = new Module_Stub();

		stub.setWillowId((Long) record.readProperty(Module.ID_PK_COLUMN));
		stub.setCreated(record.getCreated());
		stub.setDisciplineCode(record.getDisciplineCode());
		stub.setFieldOfEducation(record.getFieldOfEducation());
		stub.setIshVersion(record.getIshVersion());
		Boolean isModule = (record.getIsModule() == null) ? null
				: (record.getIsModule().intValue() == 1);
		stub.setIsModule(isModule);
		stub.setModified(record.getModified());
		stub.setNationalCode(record.getNationalCode());
		stub.setTitle(record.getTitle());
		stub.setTrainingPackageId(record.getTrainingPackageId());

		return stub;
	}
}
