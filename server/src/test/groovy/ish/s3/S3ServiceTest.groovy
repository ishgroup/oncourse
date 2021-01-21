/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.s3

import ish.common.types.AttachmentInfoVisibility
import org.apache.commons.io.IOUtils
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients

import static org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * User: akoiro
 * Date: 4/08/2016
 */
class S3ServiceTest {

	private String accessKeyId
    private String secretKey
    private String bucket
    private String region

    @Before
    void before() {
		accessKeyId = System.getProperty("storage.access.id", "AKIAIF6UAA7LXPQMRDGQ")
        secretKey = System.getProperty("storage.access.key", "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")
        bucket = System.getProperty("storage.bucket", "ish-oncourse-andromeda")
        region = System.getProperty("storage.region", "ap-southeast-2")
    }

	@Test
    void testCacheControl() throws IOException {

		InputStream stream = S3ServiceTest.class.getClassLoader().getResourceAsStream("resources/picture.jpg")
        byte[] array = IOUtils.toByteArray(stream)

        AmazonS3Service s3Service = new AmazonS3Service(accessKeyId, secretKey, bucket, region)
        AmazonS3Service.UploadResult uploadResult = s3Service.putFile(array, "picture.jpg", AttachmentInfoVisibility.PUBLIC)

        String uuid = uploadResult.getUuid()
        String versionId = uploadResult.getVersionId()
        try {
			String url = s3Service.getFileUrl(uuid, versionId, AttachmentInfoVisibility.PUBLIC)


            HttpClient httpClient = HttpClients.createDefault()
            HttpGet getMethod = new HttpGet(url)
            HttpResponse response = httpClient.execute(getMethod)

            assertEquals("inline;filename=\"picture.jpg\"", response.getHeaders("Content-Disposition")[0].getValue())
            assertEquals("max-age=604800", response.getHeaders("x-amz-meta-cache-control")[0].getValue())
        } finally {
			s3Service.removeFile(uuid)
        }
	}
}
