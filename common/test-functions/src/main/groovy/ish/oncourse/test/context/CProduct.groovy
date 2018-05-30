package ish.oncourse.test.context

import ish.oncourse.model.CourseProductRelation
import ish.oncourse.model.Product

/**
 * User: akoiro
 * Date: 26/3/18
 */
class CProduct {
    Product product

    CProduct relatedTo(CCourse from) {
        CourseProductRelation relation = new CourseProductRelation()
        relation.setCollege(this.product.college)
        relation.setCourse(from.course)
        relation.setProduct(this.product)
        relation.setCreated(new Date())
        relation.setModified(new Date())
        return this
    }
}
