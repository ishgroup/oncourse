package ish.oncourse.willow.checkout.functions

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Discount
import ish.oncourse.willow.model.common.CommonError
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

class GetDiscount extends Get<Discount> {
    final static  Logger logger = LoggerFactory.getLogger(GetContact.class)

    GetDiscount(ObjectContext context, College college, String id) {
        super(context, college, id)
    }

    @Override
    Discount get() {
        if (!StringUtils.trimToNull(id)) {
            logger.error("discount Id required")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'Discount Id required')).build())
        }
        Discount discount = (ObjectSelect.query(Discount).where(ExpressionFactory.matchDbExp(Discount.ID_PK_COLUMN, id)) & Discount.COLLEGE.eq(college)).selectOne(context)
        if (!discount) {
            logger.error("Discount is not exist, id:$id collegeId: $college.id")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'Discount is not exist')).build())
        }
        discount
    }
}
