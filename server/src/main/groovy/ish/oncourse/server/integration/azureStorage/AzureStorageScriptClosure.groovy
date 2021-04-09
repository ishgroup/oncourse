/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.integration.azureStorage

import ish.oncourse.API
import ish.oncourse.server.scripting.ScriptClosure
import ish.oncourse.server.scripting.ScriptClosureTrait

/**
 * Use this script block to easily send documents into the Azure Storage Blob service. Note that this has nothing to do with the onCourse document storage system and simply stores the blob in the Azure service without tracking it.
 *
 * ```
 * def transactions = query {
 *     entity "AccountTransaction"
 *     query "createdOn is today"
 * }
 * 
 * def bytes = export {
 *     template "ish.onCourse.accountTransactionMYOB.csv"
 *     records transactions
 * }
 *
 * azurestorage {
 *     name "integration name"
 *     blob bytes
 *     fileName "transactions.csv"
 * }
 * ```
 *
 * If you pass the FileData object in the blob attribute, you can skip the name.
 *
 *
 * ```
 * azurestorage {
 *     name "integration name"
 *     blob fileData
 * }
 * ```
 *
 */
@API
@ScriptClosure(key = "azurestorage", integration = AzureStorageIntegration)
class AzureStorageScriptClosure implements ScriptClosureTrait<AzureStorageIntegration> {

    private Object blob
    private String fileName


    void blob(Object blob) {
        this.blob = blob
    }

    void fileName(String fileName) {
        this.fileName = fileName
    }

    /**
     * Execute the closure with the configuration from the passed integration
     *
     * @param integration
     */
    @Override
    Object execute(AzureStorageIntegration integration) {
        return integration.store(blob, fileName)
    }
}
