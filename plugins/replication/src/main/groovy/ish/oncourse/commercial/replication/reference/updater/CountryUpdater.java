/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.oncourse.commercial.replication.reference.updater;

import ish.oncourse.server.cayenne.Country;
import ish.oncourse.webservices.v7.stubs.reference.CountryStub;
import org.apache.cayenne.ObjectContext;

/**
 */
public class CountryUpdater extends BaseReferenceUpdater<CountryStub> {

	public CountryUpdater(ObjectContext ctx) {
		super(ctx);
	}

	/**
	 * @see IReferenceUpdater#updateRecord(ish.oncourse.webservices.util.GenericReferenceStub)
	 */
	@Override
	public void updateRecord(CountryStub record) {
		var country = findOrCreateEntity(Country.class, record.getWillowId());
		country.setCreatedOn(record.getCreated());
		country.setIsoCodeAlpha2(record.getIsoCodeAlpha2());
		country.setIsoCodeAlpha3(record.getIsoCodeAlpha3());
		country.setIsoCodeNumeric(record.getIsoCodeNumeric());
		country.setModifiedOn(record.getModified());
		country.setName(record.getName());
		country.setSaccCode(record.getSaccCode());
		country.setWillowId(record.getWillowId());
	}
}
