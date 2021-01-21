/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import ish.common.types.AttachmentInfoVisibility;
import ish.oncourse.server.document.DocumentService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static java.nio.file.Path.of;

public class AmazonS3Service {

    private static final Long DEFAULT_URL_EXPIRE_TIMEOUT = 600000L;
    private static final long ONE_WEEK_IN_SEC = 60 * 60 * 24 * 7;
    private static final String METADATA_CacheControl = "Cache-Control";
    private static final String METADATA_CacheControlValueTemplate = "max-age=%d";
    private static final String CONTENT_DISPOSITION_TEMPLATE = "inline;filename=\"%s\"";


    private AmazonS3 s3Client;
    private AWSCredentials credentials;
    private Regions region;
    private String bucketName;

    AmazonS3Service(String accessKeyId, String secretKey, String bucketName, String region) {
        this.credentials = new BasicAWSCredentials(accessKeyId, secretKey);
        this.bucketName = bucketName;
        this.region = Regions.fromName(region);
        this.s3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(this.region)
                .build();
    }

    public AmazonS3Service(DocumentService documentService) {
        this(documentService.getAccessKeyId(), documentService.getAccessSecretKey(), documentService.getBucketName(), documentService.getRegion());
    }

    /**
     * Uploads byte array data to S3 for the first time generating UUID key.
     *
     * @param content - byte array to upload
     * @param name - file name which will be appended to UUID
     * @param visibility - visibiluty rules
     * @return generated key and versionId under which file is stored in S3
     */
    public UploadResult putFile(byte[] content, String name, AttachmentInfoVisibility visibility) throws IOException {
        String key = UUID.randomUUID().toString();

        String versionId = putFile(key, name, content, visibility);

        return new UploadResult(key, versionId);
    }


    /**
     * Uploads new file to S3 or uploads new file version of file if the file with the same UUID(key) already exist on s3.
     *
     * @param uniqueKey - UUID for uploaded file (also uses on s3)
     * @param fileName - file name which will be appended to UUID
     * @param content - file data to upload
     * @param visibility - visibiluty rules
     * @return generated versionId under which file is stored in S3
     */
    public String putFile(String uniqueKey, String fileName, byte[] content, AttachmentInfoVisibility visibility) throws IOException {
        InputStream is = new ByteArrayInputStream(content);

        ObjectMetadata metadata = new ObjectMetadata();
        String otherContentType = Files.probeContentType(of(fileName));
        metadata.setContentType(otherContentType);
        metadata.setContentLength(content.length);
        metadata.setContentDisposition(format(CONTENT_DISPOSITION_TEMPLATE, fileName));
        metadata.addUserMetadata(METADATA_CacheControl, format(METADATA_CacheControlValueTemplate, ONE_WEEK_IN_SEC));

        PutObjectRequest request = new PutObjectRequest(bucketName, uniqueKey, is, metadata);
        PutObjectResult result = s3Client.putObject(request);

        changeVisibility(uniqueKey, result.getVersionId(), visibility);

        return result.getVersionId();
    }


    /**
     * Changes stored file visibility rules. First request fetches current rules which are compared with specified,
     * if they are different second request replaces object setting new rules.
     *
     * @param uniqueKey - key under which file is stored in S3
     * @param versionId - version which visibility will be changed
     * @param visibility - visibility rules
     */
    public void changeVisibility(String uniqueKey, String versionId, AttachmentInfoVisibility visibility) {
        AccessControlList objectAcl = s3Client.getObjectAcl(bucketName, uniqueKey, versionId);

        List<AttachmentInfoVisibility> attachmentInfoVisibilityList = Arrays.asList(AttachmentInfoVisibility.PUBLIC, AttachmentInfoVisibility.LINK);

        if (attachmentInfoVisibilityList.contains(visibility)) {
            objectAcl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
        } else {
            objectAcl.revokeAllPermissions(GroupGrantee.AllUsers);
        }

        s3Client.setObjectAcl(bucketName, uniqueKey, versionId, objectAcl);
    }


    /**
     * Generates URL to access file with specified key.
     *
     * @param key - key under which file is stored in S3
     * @param visibility - file access rights
     * @return temporary URL to access specified file
     */
    public String getFileUrl(String key, AttachmentInfoVisibility visibility) {
        return getFileUrl(key, null, visibility);
    }


    /**
     * Generates URL to access file with specified key.
     *
     * @param uniqueKey - key under which file is stored in S3
     * @param versionId - version of file stored in S3     *
     * @param visibility - file access rights
     * @return temporary URL to access specified file
     */
    public String getFileUrl(String uniqueKey, String versionId, AttachmentInfoVisibility visibility) {
        return getFileUrl(uniqueKey, versionId, visibility, DEFAULT_URL_EXPIRE_TIMEOUT);
    }

    public String getFileUrl(String uniqueKey, String versionId, AttachmentInfoVisibility visibility, Long expireTimeout) {
        String url = null;
        if (!AttachmentInfoVisibility.PUBLIC.equals(visibility) && !AttachmentInfoVisibility.LINK.equals(visibility)) {
            Date expiration = new Date();
            expiration.setTime(expiration.getTime() + DEFAULT_URL_EXPIRE_TIMEOUT);

            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, uniqueKey)
                            .withVersionId(versionId)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);
            url = s3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();

        } else {
            if (versionId != null) {
                GetObjectRequest request = new GetObjectRequest(bucketName, uniqueKey)
                        .withVersionId(versionId);
                try {
                    S3Object s3Object = s3Client.getObject(request);
                    url = s3Object.getObjectContent().getHttpRequest().getURI().toString();
                } catch (AmazonS3Exception ignored) {}
            }
            if (url == null) {
                url = s3Client.getUrl(bucketName, uniqueKey).toString();
            }
        }
        return url;
    }

    /**
     * Remove file with specified key.
     *
     * @param uniqueKey - key under which file is stored in S3
     */
    public void removeFile(String uniqueKey) {
        DeleteObjectRequest request = new DeleteObjectRequest(bucketName, uniqueKey);
        s3Client.deleteObject(request);
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
