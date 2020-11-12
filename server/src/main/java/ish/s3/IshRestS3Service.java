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
package ish.s3;

import org.jets3t.service.Constants;
import org.jets3t.service.ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.security.ProviderCredentials;

import java.util.HashMap;
import java.util.Map;

/**
 * Extension of jets3t's {@link RestS3Service} to add some convenience methods and missing functionality.
 */
public class IshRestS3Service extends RestS3Service {

	public static final String PROXY_AUTODETECT_PROPERTY = "httpclient.proxy-autodetect";
	public static final String PROXY_HOST_PROPERTY = "httpclient.proxy-host";
	public static final String PROXY_PORT_PROPERTY = "httpclient.proxy-port";
	public static final String PROXY_USER_PROPERTY = "httpclient.proxy-user";
	public static final String PROXY_PASSWORD_PROPERTY = "httpclient.proxy-password";
	public static final String JNLP_PREFIX = "jnlp.";
	public static final String AMAZON_URL = "https://ish.signin.aws.amazon.com/console";

	public IshRestS3Service(ProviderCredentials credentials) {
		super(credentials);
	}



	/**
	 * Overrides default jets3t's put ACL implementation to add support for canned access control lists.
	 */
	@Override
	protected void putAclImpl(String bucketName, String objectKey, AccessControlList acl, String versionId) throws ServiceException {
		if (acl.getValueForRESTHeaderACL() != null) {
			Map<String, String> requestParameters = new HashMap<>();
			requestParameters.put("acl", "");
			if(versionId != null) {
				requestParameters.put("versionId", versionId);
			}

			Map<String, Object> metadata = new HashMap<>();
			metadata.put(Constants.REST_HEADER_PREFIX + "acl", acl.getValueForRESTHeaderACL());

			performRestPut(bucketName, objectKey, metadata, requestParameters, null, true);
		} else {
			super.putAclImpl(bucketName, objectKey, acl, versionId);
		}
	}
}
