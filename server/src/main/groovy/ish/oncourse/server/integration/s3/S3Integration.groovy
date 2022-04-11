/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.integration.s3

import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import ish.oncourse.server.messaging.DocumentParam
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetUrlRequest
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse

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
        AwsBasicCredentials credentials = AwsBasicCredentials.create(account, key)
        Region region = Region.of(this.region)
        S3Client s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build()

        DocumentParam documentParam
        if (blob instanceof DocumentParam) {
            documentParam = blob as DocumentParam
            name = documentParam.fileName
        } else {
            documentParam = DocumentParam.valueOf(name, blob)
        }
        byte[] bytes = documentParam.contentInBytes

        String uuid = UUID.randomUUID().toString()
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(uuid)
                .contentType(Files.probeContentType(of(name)))
                .contentLength((long) bytes.length)
                .contentDisposition(String.format("inline;filename=\"$name\"", name))
                .build() as PutObjectRequest
        PutObjectResponse response = s3.putObject(objectRequest, RequestBody.fromBytes(bytes))
        String version = response.versionId()

        def objectAclRequest = PutObjectAclRequest.builder()
                .bucket(bucket)
                .key(uuid)
                .versionId(version)
                .acl("public-read")
        s3.putObjectAcl(objectAclRequest.build() as PutObjectAclRequest)

        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(bucket)
                .key(uuid)
                .build()
        return s3.utilities().getUrl(request)
    }
}
