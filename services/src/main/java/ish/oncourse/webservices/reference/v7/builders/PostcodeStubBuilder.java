/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.reference.v7.builders;

import ish.oncourse.model.PostcodeDb;
import ish.oncourse.webservices.reference.services.IReferenceStubBuilder;
import ish.oncourse.webservices.v7.stubs.reference.PostcodeStub;

import java.math.BigDecimal;


public class PostcodeStubBuilder implements IReferenceStubBuilder<PostcodeDb> {
	@Override
	public PostcodeStub convert(PostcodeDb record) {
		PostcodeStub stub = new PostcodeStub();

		stub.setWillowId(record.getId());
		stub.setPostcode(record.getPostcode());
		stub.setState(record.getState());
		stub.setSuburb(record.getSuburb());
		stub.setIshVersion(record.getIshVersion());
		stub.setType(record.getType().getDatabaseValue());
		stub.setLatitude(new BigDecimal(record.getLat()));
		stub.setLongitude(new BigDecimal(record.getLon()));
		stub.setDescription(record.getDc());
		return stub;
	}
}
