/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.soap.builders;

import ish.oncourse.model.TrainingPackage;
import ish.oncourse.webservices.soap.stubs.reference.TrainingPackage_Stub;

/**
 *
 * @author marek
 */
public class TrainingPackageStubBuilder {

	public static TrainingPackage_Stub convert(TrainingPackage record) {

		TrainingPackage_Stub stub = new TrainingPackage_Stub();

		stub.setWillowId((Long) record.readProperty(TrainingPackage.ID_PK_COLUMN));
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
