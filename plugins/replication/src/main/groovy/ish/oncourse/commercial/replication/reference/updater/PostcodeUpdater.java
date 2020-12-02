/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.reference.updater;

import ish.common.types.PostcodeType;
import ish.common.types.TypesUtil;
import ish.oncourse.server.cayenne.Postcode;
import ish.oncourse.webservices.v7.stubs.reference.PostcodeStub;
import org.apache.cayenne.ObjectContext;


public class PostcodeUpdater extends BaseReferenceUpdater<PostcodeStub> {
	/**
	 * @param ctx
	 */
	public PostcodeUpdater(ObjectContext ctx) {
		super(ctx);
	}

	@Override
	public void updateRecord(PostcodeStub stub) {
		var postcode = findOrCreateEntity(Postcode.class, stub.getWillowId());
		postcode.setPostcode(stub.getPostcode().toString());
		postcode.setState(stub.getState());
		postcode.setSuburb(stub.getSuburb());
		postcode.setDescription(stub.getDescription());
		postcode.setLatitude(stub.getLatitude());
		postcode.setLongitude(stub.getLongitude());
		postcode.setType(TypesUtil.getEnumForDatabaseValue(stub.getType(), PostcodeType.class));
		postcode.setWillowId(stub.getWillowId());
	}
}
