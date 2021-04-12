/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.integration.azureStorage

import com.azure.storage.blob.BlobContainerClient
import com.azure.storage.blob.BlobContainerClientBuilder
import com.azure.storage.blob.specialized.BlockBlobClient
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import ish.oncourse.server.messaging.DocumentParam
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import com.azure.storage.common.StorageSharedKeyCredential

@Plugin(type=17)
class AzureStorageIntegration implements PluginTrait {
    public static final String AZURE_ACCOUNT = "account"
    public static final String AZURE_KEY = "key"
    public static final String AZURE_CONTAINER = "container"

    String account
    String key
    String container

    private static Logger logger = LogManager.logger

    AzureStorageIntegration(Map args) {
        loadConfig(args)

        this.account = configuration.getIntegrationProperty(AZURE_ACCOUNT).value
        this.key = configuration.getIntegrationProperty(AZURE_KEY).value
        this.container = configuration.getIntegrationProperty(AZURE_CONTAINER).value
    }



    String store(Object blob, String name) {

        BlobContainerClient blobContainerClient = new BlobContainerClientBuilder()
                .endpoint("https://${this.account}.blob.core.windows.net")
                .credential(new StorageSharedKeyCredential(this.account, this.key))
                .containerName(container)
                .buildClient()

        DocumentParam documentParam
        if (blob instanceof DocumentParam) {
            documentParam = blob as DocumentParam
            name = documentParam.fileName
        } else {
            documentParam = DocumentParam.valueOf(name, blob)
        }
        byte[] bytes = documentParam.contentInBytes
        
        BlockBlobClient blobClient = blobContainerClient.getBlobClient(name).getBlockBlobClient()
        blobClient.upload( new ByteArrayInputStream(bytes), bytes.length)
        return blobClient.blobUrl

    }
}
