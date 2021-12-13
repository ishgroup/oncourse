/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.function

import ish.oncourse.server.cayenne.AbstractTaggable
import ish.oncourse.server.cayenne.Article
import ish.oncourse.server.cayenne.Membership
import ish.oncourse.server.cayenne.ProductItem
import ish.oncourse.server.cayenne.Voucher
import ish.oncourse.server.cayenne.glue.CayenneDataObject

class AbstractTaggableFunctions {
    private static Map<Class<? extends AbstractTaggable>, List<Class<? extends CayenneDataObject>>> inheritors = new HashMap<Class<? extends AbstractTaggable>, List<Class<? extends CayenneDataObject>>>() {
        {
            put(ProductItem.class, new ArrayList<Class<? extends CayenneDataObject>>(){{
                add(Article.class)
                add(Membership.class)
                add(Voucher.class)
            }})
        }
    }

    static List<Class<? extends CayenneDataObject>> inheritorsFor(Class<? extends CayenneDataObject> superClass) {
        return inheritors.getOrDefault(superClass, List.of(superClass))
    }
}
