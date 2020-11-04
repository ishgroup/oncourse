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
