package ish.oncourse.server.api.dao

import ish.oncourse.server.cayenne.ProductCourseRelation
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById

class ProductCourseRelationDao implements CayenneLayer<ProductCourseRelation> {
    @Override
    ProductCourseRelation newObject(ObjectContext context) {
        context.newObject(ProductCourseRelation)
    }

    @Override
    ProductCourseRelation getById(ObjectContext context, Long id) {
        SelectById.query(ProductCourseRelation, id)
                .selectOne(context)
    }
}
