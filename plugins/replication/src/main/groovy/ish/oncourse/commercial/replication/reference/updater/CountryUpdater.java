/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
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
