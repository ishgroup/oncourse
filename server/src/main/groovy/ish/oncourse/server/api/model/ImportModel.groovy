/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.model

import ish.oncourse.server.cayenne.Import

class ImportModel {
    Import dbImport = null
    Map<String, Object> importData = new HashMap<>()

    Import getDbImport() {
        return dbImport
    }

    void setDbImport(Import dbImport) {
        this.dbImport = dbImport
    }

    Map<String, Object> getImportData() {
        return importData
    }

    void setImportData(Map<String, Object> importData) {
        this.importData = importData
    }

    void addimportData(String key, Object data) {
        this.importData.put(key, data)
    }
}
