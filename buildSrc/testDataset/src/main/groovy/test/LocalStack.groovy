/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package test

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.CreateBucketRequest
import groovy.transform.CompileStatic
import io.findify.s3mock.S3Mock
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

@CompileStatic
class LocalStack {

    private static S3Mock s3Mock

    static class Start extends DefaultTask {

        @Input
        @Optional
        String bucketName = "test-bucket"

        @OutputFile
        File getPropertiesFile() {
            return new File(propertiesFilePath)
        }

        @Input
        @Optional
        String propertiesFilePath = "${project.buildDir}/localstack.properties"

        @TaskAction
        void run() {
            try {
                logger.lifecycle("Starting S3Mock server...")

                int port = 8001
                String dataDir = "${project.buildDir}/s3mock-data"

                // Создаем директорию для данных
                File dataDirFile = new File(dataDir)
                if (!dataDirFile.exists()) {
                    dataDirFile.mkdirs()
                }

                // Запускаем S3Mock
                s3Mock = S3Mock.create(port, dataDir)
                s3Mock.start()

                logger.lifecycle("S3Mock server started at http://localhost:${port}")

                createBucket(bucketName, port)
                writePropertiesToFile(port)

                logger.lifecycle("S3Mock configuration prepared successfully")
            } catch (Exception e) {
                throw new RuntimeException("Error starting S3Mock server: ${e.message}", e)
            }
        }

        private void createBucket(String bucketName, int port) {
            logger.lifecycle("Creating S3 bucket {}", bucketName)

            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                            "http://localhost:${port}",
                            "us-east-1"
                    ))
                    .withPathStyleAccessEnabled(true)
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("test", "test")))
                    .build()

            if (!s3Client.doesBucketExistV2(bucketName)) {
                s3Client.createBucket(new CreateBucketRequest(bucketName))
                logger.lifecycle("S3 bucket {} created successfully", bucketName)
            }
        }

        private void writePropertiesToFile(int port) {
            logger.lifecycle("Writing S3Mock properties to: ${propertiesFilePath}")

            File propertiesFile = new File(propertiesFilePath)
            propertiesFile.parentFile.mkdirs()

            Properties properties = new Properties()
            properties.setProperty("document.accessKeyId", "test")
            properties.setProperty("document.accessSecretKey", "test")
            properties.setProperty("document.bucket", bucketName)
            properties.setProperty("document.region", "us-east-1")
            properties.setProperty("document.endpoint", "http://localhost:${port}")

            propertiesFile.withOutputStream { outputStream ->
                properties.store(outputStream, "S3Mock properties for S3")
            }
        }
    }

    static class Stop extends DefaultTask {

        @TaskAction
        void run() {
            logger.lifecycle("Attempting to stop S3Mock server...")
            if (s3Mock != null) {
                s3Mock.stop()
                logger.lifecycle("S3Mock server has been successfully stopped")
            } else {
                logger.lifecycle("No running S3Mock server found to stop")
            }
        }
    }
}