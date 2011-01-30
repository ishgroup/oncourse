/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.services.builders;

import org.apache.cayenne.Persistent;

import ish.oncourse.model.Module;
import ish.oncourse.webservices.v4.stubs.reference.ModuleStub;

/**
 *
 * @author marek
 */
public final class ModuleStubBuilder implements IStubBuilder {

	public ModuleStub convert(Persistent p) {
		
		Module record = (Module) p;
		
		ModuleStub stub = new ModuleStub();

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
