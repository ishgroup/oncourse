/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.builders.reference;

import ish.oncourse.model.TrainingPackage;
import ish.oncourse.webservices.v4.stubs.reference.TrainingPackageStub;

/**
 *
 * @author marek
 */
public final class TrainingPackageStubBuilder implements IReferenceStubBuilder<TrainingPackage> {

	public TrainingPackageStub convert(TrainingPackage record) {
		
		TrainingPackageStub stub = new TrainingPackageStub();

		stub.setWillowId(record.getId());
		stub.setCopyrightCategory(record.getCopyrightCategory());
		stub.setCopyrightContract(record.getCopyrightContact());
		stub.setCreated(record.getCreated());
		stub.setDeveloper(record.getDeveloper());
		stub.setEndorsementFrom(record.getEndorsementFrom());
		stub.setEndorsementTo(record.getEndorsementTo());
		stub.setIshVersion(record.getIshVersion());
		stub.setModified(record.getModified());
		stub.setNationalISC(record.getNationalISC());
		stub.setPurchaseFrom(record.getPurchaseFrom());
		stub.setTitle(record.getTitle());
		stub.setType(record.getType());

		return stub;
	}
}
