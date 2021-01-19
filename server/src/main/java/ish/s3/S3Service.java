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

import ish.common.types.AttachmentInfoVisibility;
import ish.oncourse.common.ResourcesUtil;
import ish.oncourse.server.document.DocumentService;
import org.jets3t.service.Constants;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.multi.StorageServiceEventListener;
import org.jets3t.service.multi.s3.ThreadedS3Service;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.utils.Mimetypes;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

/**
 * File service operating with Amazon S3 API.
 */
public class S3Service {

	private static final Long DEFAULT_URL_EXPIRE_TIMEOUT = 600000L; // 10 minutes

	private static final String METADATA_CacheControl = "Cache-Control";
	private static final String REST_METADATA_CacheControl = Constants.REST_METADATA_PREFIX + METADATA_CacheControl;
	private static final String METADATA_CacheControlValueTemplate = "max-age=%d";


	private static final long ONE_WEEK_IN_SEC = 60 * 60 * 24 * 7;

	private static final String CONTENT_DISPOSITION_TEMPLATE = "inline;filename=\"%s\"";
	private static final String URL_PARAMETER_TEMPLATE = "%s?%s=%s";

	private static final String VERSION_ID = "versionId";

	private String bucketName;
	private String region;
	private AWSCredentials credentials;

	public S3Service(String accessKeyId, String secretKey, String bucketName, String region) {
		this.credentials = new AWSCredentials(accessKeyId, secretKey);
		this.bucketName = bucketName;
		this.region = region;
	}

	public S3Service(DocumentService documentService) {
		this(documentService.getAccessKeyId(), documentService.getAccessSecretKey(), documentService.getBucketName(), documentService.getRegion());
	}

	/**
	 * Uploads file to S3 for the first time generating UUID key.
	 *
	 * @param file - file to upload
	 * @param name - file name which will be appended to UUID
	 * @param visibility - visibiluty rules
	 * @return generated key and versionId under which file is stored in S3
	 */
	public UploadResult putFile(File file, String name, AttachmentInfoVisibility visibility, StorageServiceEventListener listener) throws NoSuchAlgorithmException, ServiceException, IOException {
		String key = UUID.randomUUID().toString();

		String versionId = putFile(key, name, ResourcesUtil.fileToByteArray(file), visibility, listener);

		return new UploadResult(key, versionId);
	}

	public UploadResult putFile(byte[] content, String name, AttachmentInfoVisibility visibility, StorageServiceEventListener listener) throws NoSuchAlgorithmException, ServiceException, IOException {
		String key = UUID.randomUUID().toString();
		String versionId = putFile(key, name, content, visibility, listener);
		return new UploadResult(key, versionId);
	}

	/**
	 * Uploads byte array data to S3 for the first time generating UUID key.
	 *
	 * @param data - byte array to upload
	 * @param name - file name which will be appended to UUID
	 * @param visibility - visibiluty rules
	 * @return generated key and versionId under which file is stored in S3
	 */
	public UploadResult putFile(byte[] data, String name, AttachmentInfoVisibility visibility) throws ServiceException, NoSuchAlgorithmException, IOException {
		String key = UUID.randomUUID().toString();

		String versionId = putFile(key, name, data, visibility, null);

		return new UploadResult(key, versionId);
	}

	/**
	 * Uploads new file to S3 or uploads new file version of file if the file with the same UUID(key) already exist on s3.
	 *
	 * @param key - UUID for uploaded file (also uses on s3)
	 * @param name - file name which will be appended to UUID
	 * @param data - file data to upload
	 * @param visibility - visibiluty rules
	 * @param listener - listener for control upload events, progress monitoring and interruption uploading if needed
	 * @return generated versionId under which file is stored in S3
	 */
	public String putFile(String key, String name, byte[] data, AttachmentInfoVisibility visibility, StorageServiceEventListener listener) throws NoSuchAlgorithmException, ServiceException, IOException {

		S3Object s3Object = new S3Object(key, data);
		s3Object.setKey(key);

		Mimetypes mimetypes = Mimetypes.getInstance();
		s3Object.setContentType(mimetypes.getMimetype(name));

		String value = format(METADATA_CacheControlValueTemplate, ONE_WEEK_IN_SEC);
		s3Object.addMetadata(REST_METADATA_CacheControl, value);
		s3Object.addMetadata(METADATA_CacheControl, value);

		if (AttachmentInfoVisibility.PUBLIC.equals(visibility) || AttachmentInfoVisibility.LINK.equals(visibility)) {
			s3Object.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);
		} else {
			s3Object.setAcl(AccessControlList.REST_CANNED_PRIVATE);
		}

		s3Object.setContentDisposition(format(CONTENT_DISPOSITION_TEMPLATE, name));

		if (listener != null) {
			putObjects(listener, s3Object);
		} else {
			putObject(s3Object);
		}

		return s3Object.getVersionId();
	}

	private void putObject(S3Object s3Object) throws S3ServiceException {
		IshRestS3Service s3Service = new IshRestS3Service(credentials);
		s3Service.putObject(bucketName, s3Object);
	}

	private void putObjects(StorageServiceEventListener listener, S3Object... s3Objects) throws ServiceException {
		IshRestS3Service s3Service = new IshRestS3Service(credentials);
		ThreadedS3Service threadedS3Service = new ThreadedS3Service(s3Service, listener);

		threadedS3Service.putObjects(bucketName, s3Objects);
	}

	/**
	 * Replaces existing file in S3 with a new version. Old version will still be accessible if bucket versioning is enabled.
	 *
	 * @param key - key under which file is stored in S3
	 * @param newFile - new file
	 * @param visibility - visibility rules
	 *
	 * @return generated key under which file is stored in S3
	 */
	public UploadResult replaceFile(String key, String name, File newFile, AttachmentInfoVisibility visibility, StorageServiceEventListener listener) throws NoSuchAlgorithmException, ServiceException, IOException {
		String versionId = putFile(key, name, ResourcesUtil.fileToByteArray(newFile), visibility, listener);
		return new UploadResult(key, versionId);
	}

	/**
	 * Changes stored file visibility rules. First request fetches current rules which are compared with specified,
	 * if they are different second request replaces object setting new rules.
	 *
	 * @param key - key under which file is stored in S3
	 * @param visibility - visibility rules
	 * @param versionIds - versions which visibility will be changed
	 */
	public void changeVisibility(String key, List<String> versionIds,  AttachmentInfoVisibility visibility) throws ServiceException {
		IshRestS3Service s3Service = new IshRestS3Service(credentials);

		AccessControlList acl = (AttachmentInfoVisibility.PUBLIC.equals(visibility) || AttachmentInfoVisibility.LINK.equals(visibility)) ? AccessControlList.REST_CANNED_PUBLIC_READ : AccessControlList.REST_CANNED_PRIVATE;

		for (String versionId : versionIds) {
			s3Service.putVersionedObjectAcl(versionId, bucketName, key, acl);
		}
	}

	/**
	 * Generates URL to access file with specified key.
	 *
	 * @param key - key under which file is stored in S3
	 * @param visibility - file access rights
	 * @param versionId - version of file stored in S3
	 * @param expireTimeout - timeout for URL to expire
	 * @return temporary URL to access specified file
	 */
	public String getFileUrl(String key, AttachmentInfoVisibility visibility, String versionId, Long expireTimeout) throws MalformedURLException {
		IshRestS3Service s3Service = new IshRestS3Service(credentials);

		String url;
			// generate URL without version
		if (!AttachmentInfoVisibility.PUBLIC.equals(visibility) && !AttachmentInfoVisibility.LINK.equals(visibility)) {

			Date expiration = new Date();
			expiration.setTime(expiration.getTime() + expireTimeout);


			// add versionId parameter to URL if needed
			if (versionId != null) {
				url = s3Service.createSignedUrl("GET", bucketName, key, format("versionId=%s", versionId) , null, expiration.getTime() / 1000, false);
			} else {
				url = s3Service.createSignedGetUrl(bucketName, key, expiration, false);
			}

		} else {
			// generate URL without version
			url = s3Service.createUnsignedObjectUrl(bucketName, key, false, true, false);

			// add versionId parameter to URL if needed
			if (versionId != null) {
				url = format(URL_PARAMETER_TEMPLATE, url, VERSION_ID, versionId);
			}
		}

		return url;
	}

	/**
	 * Generates URL to access file with specified key.
	 *
	 * @param key - key under which file is stored in S3
	 * @param visibility - file access rights
	 * @param versionId - version of file stored in S3
	 * @return temporary URL to access specified file
	 */
	public String getFileUrl(String key, AttachmentInfoVisibility visibility, String versionId) throws MalformedURLException {
		return getFileUrl(key, visibility, versionId, DEFAULT_URL_EXPIRE_TIMEOUT);
	}

	/**
	 * Generates URL to access file with specified key.
	 *
	 * @param key - key under which file is stored in S3
	 * @param visibility - file access rights
	 * @return temporary URL to access specified file
	 */
	public String getFileUrl(String key, AttachmentInfoVisibility visibility) throws MalformedURLException {
		return getFileUrl(key, visibility, null);
	}

	/**
	 * Remove file with specified key.
	 * on s3: marked file deleted; and create new versionId to this mark;
	 *
	 * @param key - key under which file is stored in S3
	 */
	public void removeFile(String key) throws ServiceException {
		IshRestS3Service s3Service = new IshRestS3Service(credentials);
		s3Service.deleteObject(bucketName, key);
	}

	/**
	 * Remove version file with specified key and versionId.
	 * on s3: completely delete version of file
	 *
	 * @param key       - key under which file is stored in S3
	 * @param versionId - version of file stored in S3
	 */
	public void removeVersionOfFile(String key, String versionId) throws S3ServiceException {
		IshRestS3Service s3Service = new IshRestS3Service(credentials);
		s3Service.deleteVersionedObject(versionId, bucketName, key);
	}

	public static class UploadResult {

		private String uuid;
		private String versionId;

		public UploadResult(String uuid, String versionId) {
			this.uuid = uuid;
			this.versionId = versionId;
		}

		public String getVersionId() {
			return versionId;
		}

		public String getUuid() {
			return uuid;
		}
	}
}
