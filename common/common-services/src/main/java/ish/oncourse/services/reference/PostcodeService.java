/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.reference;

import ish.oncourse.model.PostcodeDb;


public class PostcodeService extends AbstractReferenceService<PostcodeDb> implements IPostcodeService {
	
	@Override
	public String getDbEntityName() {
		return "postcode_db";
	}

}
