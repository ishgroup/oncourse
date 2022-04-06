/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.s3;

import ish.common.types.AttachmentInfoVisibility;
import ish.oncourse.server.document.DocumentService;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.*;

import static java.lang.String.format;
import static java.nio.file.Path.of;

public class AmazonS3Service {

    private static final long ONE_WEEK_IN_SEC = 60 * 60 * 24 * 7;
    private static final String METADATA_CacheControl = "Cache-Control";
    private static final String METADATA_CacheControlValueTemplate = "max-age=%d";
    private static final String CONTENT_DISPOSITION_TEMPLATE = "inline;filename=\"%s\"";


    private final S3Client s3Client;
    private final String bucketName;
    private final S3Presigner presigner;

    AmazonS3Service(String accessKeyId, String secretKey, String bucketName, String regionName) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretKey);
        Region region = Region.of(regionName);
        this.bucketName = bucketName;
        this.s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
        this.presigner = S3Presigner.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
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
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueKey)
                .contentType(Files.probeContentType(of(fileName)))
                .contentLength((long) content.length)
                .contentDisposition(format(CONTENT_DISPOSITION_TEMPLATE, fileName))
                .metadata(Map.of(METADATA_CacheControl, format(METADATA_CacheControlValueTemplate, ONE_WEEK_IN_SEC)))
                .build();

        var response = s3Client.putObject(objectRequest, RequestBody.fromBytes(content));

        changeVisibility(uniqueKey, response.versionId(), visibility);

        return response.versionId();
    }


    /**
     * Changes stored file visibility rules.
     *
     * @param uniqueKey - key under which file is stored in S3
     * @param versionId - version which visibility will be changed
     * @param visibility - visibility rules
     */
    public void changeVisibility(String uniqueKey, String versionId, AttachmentInfoVisibility visibility) {
        List<AttachmentInfoVisibility> attachmentInfoVisibilityList = Arrays.asList(AttachmentInfoVisibility.PUBLIC, AttachmentInfoVisibility.LINK);
        String acl;

        if (attachmentInfoVisibilityList.contains(visibility)) {
            acl = "public-read";
        } else {
            acl = "private";
        }

        var objectAclRequest = PutObjectAclRequest.builder()
                .bucket(bucketName)
                .key(uniqueKey)
                .versionId(versionId)
                .acl(acl);

        s3Client.putObjectAcl(objectAclRequest.build());
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
        return getFileUrl(uniqueKey, versionId, visibility, ONE_WEEK_IN_SEC);
    }

    public String getFileUrl(String uniqueKey, String versionId, AttachmentInfoVisibility visibility, Long expireTimeout) {
        String url = null;
        if (AttachmentInfoVisibility.PUBLIC.equals(visibility) || AttachmentInfoVisibility.LINK.equals(visibility)) {
            return null;
        }

        var expiration = Duration.ofSeconds(expireTimeout);

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueKey)
                .versionId(versionId)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .getObjectRequest(request)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest);
        return presignedGetObjectRequest.url().toString();
    }

    /**
     * Remove file with specified key.
     *
     * @param uniqueKey - key under which file is stored in S3
     */
    public void removeFile(String uniqueKey) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueKey)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
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
