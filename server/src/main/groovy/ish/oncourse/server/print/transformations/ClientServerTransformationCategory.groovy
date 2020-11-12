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

package ish.oncourse.server.print.transformations

import ish.oncourse.cayenne.PersistentObjectI
import ish.print.AdditionalParameters
import ish.print.transformations.ContactAmountOwingSqlTransformation
import ish.print.transformations.PrintTransformation
import org.apache.cayenne.ObjectContext

/**
 * Created by akoiro on 11/06/2016.
 */
class ClientServerTransformationCategory {
    static List<PersistentObjectI> applyTransformation(PrintTransformation self, ObjectContext context,
                                                              List<Long> sourceIds,
                                                              Map<String, Object> additionalValues) {
        switch (self.class) {
            case ContactAmountOwingSqlTransformation:
                ApplyContactAmountOwingSqlTransformation transformation = new ApplyContactAmountOwingSqlTransformation(
                        context: context, date: additionalValues.get(AdditionalParameters.DATERANGE_TO.toString()) as Date
                )
                return transformation.apply()
            default:
                return self.applyTransformation(context, sourceIds, additionalValues)
        }

    }
}
