/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.util;

import ish.common.types.InvoiceType;
import ish.oncourse.server.cayenne.AbstractInvoice;
import ish.oncourse.server.cayenne.Invoice;
import ish.oncourse.server.cayenne.Quote;
import org.apache.cayenne.exp.Expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbstractEntitiesUtil {
    private static final List<String> abstractInvoiceImplClassesNames = new ArrayList<>() {
        {
            add(Invoice.class.getSimpleName());
            add(Quote.class.getSimpleName());
        }
    };

    private static final Map<String, List<String>> abstractEntityImpls =
            new HashMap<>() {{
                put(AbstractInvoice.class.getSimpleName(), abstractInvoiceImplClassesNames);
            }};

    /**
     * @param entityName - ENTITY_NAME property of data object
     * @return list of names of inheritors classes or list with entityname itself if abstract entity not recognized
     */
    public static List<String> getImplsOf(String entityName) {
        if (abstractEntityImpls.containsKey(entityName))
            return abstractEntityImpls.get(entityName);
        else
            return List.of(entityName);
    }


    /**
     * @param entityName - simple name of class
     * @return true if this class added to abstractEntityImpls map as abstract entity
     */
    public static boolean isAbstract(String entityName){
        return abstractEntityImpls.containsKey(entityName);
    }

    /**
     * @param abstractEntity - simple name of class of abstract entity for which expression will be in use
     * @param currentEntity - simple name of inheritor class of abstractEntity class
     * @return Expression - expression, which used in query with abstract entity class returns only objects of currentEntity class
     * or null if abstractEntity is not abstract or exp for it is not implemented
     */
    public static Expression getIsCurrentInheritorExp(String abstractEntity, String currentEntity){
        if(!isAbstract(abstractEntity))
            return null;
        switch (abstractEntity){
            case AbstractInvoice.ENTITY_NAME:
                return AbstractInvoice.TYPE.eq(InvoiceType.valueOf(currentEntity.toUpperCase()));
            default:
                return null;
        }
    }


}
