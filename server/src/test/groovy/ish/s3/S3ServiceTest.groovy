/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.s3

import ish.common.types.AttachmentInfoVisibility
//import org.apache.commons.httpclient.HttpClient
//import org.apache.commons.httpclient.HttpMethod
//import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.io.IOUtils
import org.jets3t.service.ServiceException
import static org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

import java.security.NoSuchAlgorithmException

/**
 * User: akoiro
 * Date: 4/08/2016
 */
class S3ServiceTest {

	private String accessKeyId
    private String secretKey
    private String bucket

    @Before
    void before() {
		accessKeyId = System.getProperty("storage.access.id", "AKIAIF6UAA7LXPQMRDGQ")
        secretKey = System.getProperty("storage.access.key", "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")
        bucket = System.getProperty("storage.bucket", "ish-oncourse-andromeda")
    }

	@Test
    void testCacheControl() throws IOException, ServiceException, NoSuchAlgorithmException {

		InputStream stream = S3ServiceTest.class.getClassLoader().getResourceAsStream("resources/picture.jpg")
        byte[] array = IOUtils.toByteArray(stream)

        S3Service s3Service = new S3Service(accessKeyId, secretKey, bucket)
        S3Service.UploadResult uploadResult = s3Service.putFile(array, "picture.jpg", AttachmentInfoVisibility.PUBLIC)

        String uuid = uploadResult.getUuid()
        try {
			String url = s3Service.getFileUrl(uuid, AttachmentInfoVisibility.PUBLIC)
//            HttpClient httpClient = new HttpClient()
//            HttpMethod method = HttpMethod.fromString(url)
//            httpClient.executeMethod(method)
//            assertEquals("max-age=604800", method.getResponseHeader("Cache-Control").getValue())
//            assertEquals("max-age=604800", method.getResponseHeader("x-amz-meta-cache-control").getValue())
        } finally {
			s3Service.removeFile(uuid)
        }
	}
}
