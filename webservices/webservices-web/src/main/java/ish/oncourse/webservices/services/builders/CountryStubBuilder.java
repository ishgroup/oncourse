/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.services.builders;

import org.apache.cayenne.Persistent;

import ish.oncourse.model.Country;
import ish.oncourse.webservices.v4.stubs.reference.CountryStub;

/**
 *
 * @author marek
 */
public final class CountryStubBuilder implements IStubBuilder {
	
	public CountryStub convert(Persistent p) {

		Country record = (Country) p;
		
		CountryStub stub = new CountryStub();

		stub.setWillowId((Long) record.readProperty(Country.ID_PK_COLUMN));
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
