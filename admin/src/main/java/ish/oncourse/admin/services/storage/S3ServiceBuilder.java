/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.admin.services.storage;

import ish.oncourse.configuration.Configuration;
import ish.oncourse.services.s3.IS3Service;
import ish.oncourse.services.s3.S3Service;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;

import static ish.oncourse.configuration.Configuration.AdminProperty.*;

public class S3ServiceBuilder implements ServiceBuilder<IS3Service> {

	@Override
	public IS3Service buildService(ServiceResources resources) {

		String s3AccessId = Configuration.getValue(STORAGE_ACCESS_ID);
		String s3AccessKey = Configuration.getValue(STORAGE_ACCESS_KEY);
		
		if (s3AccessId == null || s3AccessKey == null) {
			throw new IllegalStateException("S3 administrative account is not set up.");
		}
		
		return new S3Service(s3AccessId, s3AccessKey);
	}
}
