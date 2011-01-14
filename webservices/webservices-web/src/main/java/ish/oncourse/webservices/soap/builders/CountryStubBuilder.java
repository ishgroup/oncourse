/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ish.oncourse.webservices.soap.builders;

import ish.oncourse.model.Country;
import ish.oncourse.webservices.soap.stubs.reference.Country_Stub;

/**
 *
 * @author marek
 */
public class CountryStubBuilder {


	public static Country_Stub convert(Country record) {

		Country_Stub stub = new Country_Stub();

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
