/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.reference.builders;

import ish.oncourse.model.Module;
import ish.oncourse.webservices.v4.stubs.reference.ModuleStub;

import java.util.Date;

/**
 *
 * @author marek
 */
public final class ModuleStubBuilder implements IReferenceStubBuilder<Module> {

	public ModuleStub convert(Module record) {
		
		ModuleStub stub = new ModuleStub();

		stub.setWillowId(record.getId());
		if (record.getCreated() == null) {
			record.setCreated(new Date());
		}
		if (record.getModified() == null) {
			record.setModified(new Date());
		}
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
