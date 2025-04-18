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
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3

@CompileStatic
class LocalStack {

    private static LocalStackContainer container

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
                logger.lifecycle("Starting Localstack container...")
                container = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest")).withServices(S3)
                container.start()

                logger.lifecycle("Localstack container started at ${container.getEndpoint()}")

                createBucket(bucketName)
                writePropertiesToFile()

                logger.lifecycle("Localstack configuration prepared successfully")
            } catch (Exception e) {
                throw new RuntimeException("Error starting Localstack container: ${e.message}", e)
            }
        }

        private void createBucket(String bucketName) {
            logger.lifecycle("Creating S3 bucket {}", bucketName)

            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                            container.getEndpoint().toString(),
                            container.getRegion()
                    ))
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                            container.getAccessKey(),
                            container.getSecretKey()
                    )))
                    .build()

            s3Client.createBucket(new CreateBucketRequest(bucketName))

            logger.lifecycle("S3 bucket {} created successfully", bucketName)
        }

        private void writePropertiesToFile() {
            logger.lifecycle("Writing Localstack properties to: ${propertiesFilePath}")

            File propertiesFile = new File(propertiesFilePath)
            propertiesFile.parentFile.mkdirs()

            Properties properties = new Properties()
            properties.setProperty("document.accessKeyId", container.getAccessKey())
            properties.setProperty("document.accessSecretKey", container.getSecretKey())
            properties.setProperty("document.bucket", bucketName)
            properties.setProperty("document.region", container.getRegion())
            properties.setProperty("document.endpoint", container.getEndpointOverride(S3).toString())

            propertiesFile.withOutputStream { outputStream ->
                properties.store(outputStream, "Localstack properties for S3")
            }
        }
    }

    static class Stop extends DefaultTask {

        @TaskAction
        void run() {
            logger.lifecycle("Attempting to stop Localstack container...")
            if (container != null) {
                container.stop()
                logger.lifecycle("Localstack container has been successfully stopped")
            } else {
                logger.lifecycle("No running Localstack container found to stop")
            }
        }
    }
}
