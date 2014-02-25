/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.admin.services.storage;

import ish.oncourse.services.s3.IS3Service;
import ish.oncourse.services.s3.S3Service;
import ish.oncourse.util.ContextUtil;
import org.apache.tapestry5.ioc.ServiceBuilder;
import org.apache.tapestry5.ioc.ServiceResources;

public class S3ServiceBuilder implements ServiceBuilder<IS3Service> {

	@Override
	public IS3Service buildService(ServiceResources resources) {

		String s3AccessId = ContextUtil.getStorageAccessId();
		String s3AccessKey = ContextUtil.getStorageAccessKey();
		
		if (s3AccessId == null || s3AccessKey == null) {
			throw new IllegalStateException("S3 administrative account is not set up.");
		}
		
		return new S3Service(s3AccessId, s3AccessKey);
	}
}
