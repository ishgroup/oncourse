/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.util;

import ish.oncourse.aql.model.Entity;
import ish.oncourse.cayenne.TaggableClasses;
import ish.oncourse.server.cayenne.AbstractInvoice;

import java.util.List;

public class TaggableUtil {

    public static TaggableClasses resolveTaggableClass(Entity entity) {
        var name = capitalizedAsConstant(entity.getName());
        if(entity.getName().equals(AbstractInvoice.class.getSimpleName())){
            return TaggableClasses.INVOICE;
        }
        return TaggableClasses.valueOf(name);
    }

    private static String capitalizedAsConstant(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }

        var charArray = name.toCharArray();
        var buffer = new StringBuilder();

        for (var i = 0; i < charArray.length; i++) {
            if ((Character.isUpperCase(charArray[i])) && (i != 0)) {
                var prevChar = charArray[i - 1];
                if ((Character.isLowerCase(prevChar))) {
                    buffer.append("_");
                }
            }
            buffer.append(Character.toUpperCase(charArray[i]));
        }

        return buffer.toString();
    }
}
