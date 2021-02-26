/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.integration.s3

import ish.oncourse.API
import ish.oncourse.server.scripting.ScriptClosure
import ish.oncourse.server.scripting.ScriptClosureTrait

/**
 * Use this script block to easily send documents into the S3 service. Note that this has nothing to do with the onCourse document storage system and simply stores the blob in the S3 service without tracking it.
 *
 * ```
 * s3 {
 *     name "integration name"
 *     blob myDoc
 *     fileName "duck.jpg"
 * }
 * ```
 *
 * If you pass the FileData object in the blob attribute, you can skip the name.
 *
 * ```
 * s3 {
 *     name "integration name"
 *     blob fileData
 * }
 * ```
 *
 */
@API
@ScriptClosure(key = "s3", integration = S3Integration)
class S3ScriptClosure implements ScriptClosureTrait<S3Integration> {

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
    Object execute(S3Integration integration) {
        return integration.stroe(blob, fileName)
    }
}
