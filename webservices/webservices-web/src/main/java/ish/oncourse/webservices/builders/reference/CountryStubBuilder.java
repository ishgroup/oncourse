/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.builders.reference;

import org.apache.cayenne.Persistent;

import ish.oncourse.model.Country;
import ish.oncourse.webservices.builders.IReferenceStubBuilder;
import ish.oncourse.webservices.v4.stubs.reference.CountryStub;

/**
 *
 * @author marek
 */
public final class CountryStubBuilder implements IReferenceStubBuilder {
	
	public CountryStub convert(Persistent entity) {
		
		Country record = (Country) entity;
		
		CountryStub stub = new CountryStub();

		stub.setWillowId(record.getId());
		stub.setAsccssCode(record.getAsccssCode());
		stub.setCreated(record.getCreated());
		stub.setIshVersion(record.getIshVersion());
		stub.setIsoCodeAlpha2(record.getIsoCodeAlpha2());
		stub.setIsoCodeAlpha3(record.getIsoCodeAlpha3());
		stub.setIsoCodeNumeric(record.getIsoCodeNumeric());
		stub.setModified(record.getModified());
		stub.setName(record.getName());
		stub.setSaccCode(record.getSaccCode());

		return stub;
	}
}
