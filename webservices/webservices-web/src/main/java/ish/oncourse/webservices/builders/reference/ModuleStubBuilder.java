/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.builders.reference;

import org.apache.cayenne.Persistent;

import ish.oncourse.model.Module;
import ish.oncourse.webservices.builders.IReferenceStubBuilder;
import ish.oncourse.webservices.v4.stubs.reference.ModuleStub;

/**
 *
 * @author marek
 */
public final class ModuleStubBuilder implements IReferenceStubBuilder {

	public ModuleStub convert(Persistent entity) {
		
		Module record = (Module) entity;
		
		ModuleStub stub = new ModuleStub();

		stub.setWillowId(record.getId());
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
