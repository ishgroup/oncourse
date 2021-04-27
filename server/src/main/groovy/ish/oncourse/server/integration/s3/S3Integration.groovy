/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.integration.s3

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.AccessControlList
import com.amazonaws.services.s3.model.GroupGrantee
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.Permission
import com.amazonaws.services.s3.model.PutObjectRequest
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import ish.oncourse.server.messaging.DocumentParam

import java.nio.file.Files

import static java.nio.file.Path.of

@Plugin(type=16)
class S3Integration implements PluginTrait {
    public static final String S3_ACCOUNT = "account"
    public static final String S3_KEY = "key"
    public static final String S3_BUCKET = "bucket"
    public static final String S3_REGION = "region"

    String account
    String key
    String bucket
    String region


    S3Integration(Map args) {
        loadConfig(args)

        this.account = configuration.getIntegrationProperty(S3_ACCOUNT).value
        this.key = configuration.getIntegrationProperty(S3_KEY).value
        this.bucket = configuration.getIntegrationProperty(S3_BUCKET).value
        this.region = configuration.getIntegrationProperty(S3_REGION).value
    }
    
    String store(Object blob, String name) {

        AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(account, key)))
                .withRegion(region)
                .build()

        DocumentParam documentParam
        if (blob instanceof DocumentParam) {
            documentParam = blob as DocumentParam
            name = documentParam.fileName
        } else {
            documentParam = DocumentParam.valueOf(name, blob)
        }
        byte[] bytes = documentParam.contentInBytes

        InputStream is = new ByteArrayInputStream(bytes)

        ObjectMetadata metadata = new ObjectMetadata()
        String otherContentType = Files.probeContentType(of(name))
        metadata.contentType = otherContentType
        metadata.contentLength = bytes.length
        metadata.contentDisposition = "inline;filename=\"$name\""
        String uuid = UUID.randomUUID().toString()

        PutObjectRequest request = new PutObjectRequest(bucket, uuid, is, metadata)
        String version = s3.putObject(request).versionId

        AccessControlList objectAcl = s3.getObjectAcl(bucket, uuid, version)
        objectAcl.grantPermission(GroupGrantee.AllUsers, Permission.Read)
        s3.setObjectAcl(bucket, uuid, version, objectAcl)
        return s3.getUrl(bucket, uuid).toString()
         
    }
}
