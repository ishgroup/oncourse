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

package ish.oncourse.server.api.dao

import ish.oncourse.server.cayenne.ArticleProduct
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

class ArticleProductDao implements CayenneLayer<ArticleProduct> {

    @Override
    ArticleProduct newObject(ObjectContext context) {
        context.newObject(ArticleProduct)
    }

    @Override
    ArticleProduct getById(ObjectContext context, Long id) {
        SelectById.query(ArticleProduct, id)
                .selectOne(context)
    }

    ArticleProduct getByCode(ObjectContext context, String code) {
        ObjectSelect.query(ArticleProduct)
                .where(ArticleProduct.SKU.eq(code))
                .selectOne(context)
    }
}
