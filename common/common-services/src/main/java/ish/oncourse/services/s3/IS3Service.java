/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.s3;

import com.amazonaws.services.identitymanagement.model.AccessKey;
import com.amazonaws.services.s3.model.Bucket;

public interface IS3Service {

	/**
	 * Creates new AWS user with get/put/delete access writes for specified bucket.
	 * 
	 * @param userName - name of a new user
	 * @param bucketName - name of the bucket to which user will have access
	 * @return access key pair for created user
	 */
	AccessKey createS3User(String userName, String bucketName);

	/**
	 * Creates new S3 bucket.
	 * 
	 * @param name - name of a bucket
	 * @return {@link Bucket} object for the created bucket
	 */
	Bucket createBucket(String name);

	/**
	 * Generates temporary URL to access specified file.
	 * 
	 * @param bucketName - bucket in which file is stored
	 * @param fileKey - file unique key
	 * @param expireTimeout - URL expire timeout
	 * @return - URL string
	 */
	String getTemporaryUrl(String bucketName, String fileKey, Long expireTimeout);

	/**
	 * Generates permanent static URL to access file with public visibility.
	 * 
	 * @param bucketName - bucket in which file is stored
	 * @param fileKey - file unique key
	 * @return - URL string
	 */
	String getPermanentUrl(String bucketName, String fileKey);
}
