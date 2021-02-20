/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.integration.azureStorage

import com.azure.storage.blob.BlobServiceClientBuilder
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import ish.print.PrintResult
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


    protected store(Object blob, String name) {
        def endpoint = "https://${this.container}.blob.core.windows.net"
        def credential = new StorageSharedKeyCredential(this.account, this.key)
        def storageClient = new BlobServiceClientBuilder().endpoint(endpoint).credential(credential).buildClient()
        def blobContainerClient = storageClient.getBlobContainerClient(this.container)

        byte[] bytes
        if (blob instanceof PrintResult) {
            bytes = (blob as PrintResult).result
            name = (blob as PrintResult).reportName
        } else {
            def bos = new ByteArrayOutputStream()
            def out = new ObjectOutputStream(bos).writeObject(blob)
            out.flush()
            bytes = bos.toByteArray()
        }

        blobContainerClient.getBlobClient(name).getBlockBlobClient().upload( new ByteArrayInputStream(bytes), bytes.length)
    }
}
