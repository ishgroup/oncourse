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

package ish.oncourse.server.api.v1.function

import ish.oncourse.server.cayenne.glue.CayenneDataObject

class EntityFunctions {

    static void moveToManyEntityRelationshipToA(CayenneDataObject source, CayenneDataObject destination, String property) {
        List<CayenneDataObject> sourceRelations = new ArrayList<>((List<CayenneDataObject>) source.getValueForKey(property))
        List<CayenneDataObject> initialDestinationRelations = new ArrayList<>((List<CayenneDataObject>) destination.getValueForKey(property))

        for (CayenneDataObject po : sourceRelations) {
            if (!initialDestinationRelations.contains(po)) {
                source.removeValueForKey(property, po)
                destination.addValueForKey(property, po)
            }
        }
    }

    static moveEntityFieldToA(CayenneDataObject a, CayenneDataObject b, String attrKey, List<String> propertiesForSkip) {
        if (!propertiesForSkip.contains(attrKey)) {
            // IMPORTANT: readProperty need to resolve fault before setting value.
            a.readProperty(attrKey)
            a.setValueForKey(attrKey, b.getValueForKey(attrKey))
        }
    }
}
