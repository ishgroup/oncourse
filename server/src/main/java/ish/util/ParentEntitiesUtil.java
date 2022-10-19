/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.util;

import ish.oncourse.cayenne.PersistentObjectI;

public class ParentEntitiesUtil {

    /**
     * @param expectedSuperEntityName - simple name of class of super entity (or not)
     * @param currentEntityName       - simple name of inheritor class of super entity (or not)
     * @return boolean - if current entity class is an inheritor of expected super entity class
     */
    public static boolean isChildOf(String currentEntityName, String expectedSuperEntityName) {
        Class<PersistentObjectI> currEntityClass = EntityUtil.entityClassForName(currentEntityName);
        Class<? super PersistentObjectI> superclass = currEntityClass.getSuperclass();
        while (superclass != null) {
            if (superclass.getSimpleName().equals(expectedSuperEntityName))
                return true;
            superclass = superclass.getSuperclass();
        }
        return false;
    }
}
