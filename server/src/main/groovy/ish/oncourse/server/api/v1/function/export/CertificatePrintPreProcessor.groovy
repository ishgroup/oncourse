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

package ish.oncourse.server.api.v1.function.export

import ish.oncourse.server.cayenne.Certificate
import org.apache.cayenne.CayenneDataObject

import java.time.LocalDate

class CertificatePrintPreProcessor extends PrintPreProcessor {

    @Override
    void preProcessEntity(CayenneDataObject dataObject) {
        if (dataObject instanceof Certificate) {
            LocalDate today = LocalDate.now()
            if (dataObject.issuedOn == null) {
                dataObject.issuedOn = today
            }
            dataObject.printedOn = today
        }
    }
}
