package ish.oncourse.willow.functions.voucher

import ish.common.types.ProductStatus
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.Voucher
import ish.oncourse.willow.checkout.functions.Get
import ish.oncourse.willow.model.common.CommonError
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

class GetVoucher extends Get<Voucher> {

    final static  Logger logger = LoggerFactory.getLogger(GetVoucher.class)

    GetVoucher(ObjectContext context, College college, String id) {
        super(context, college, id)
    }


    Voucher get() {
        if (!StringUtils.trimToNull(id)) {
            logger.error("redeemed voucher Id required")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'redeemed voucher id required')).build())
        }
        Voucher voucher = (ObjectSelect.query(Voucher).where(ExpressionFactory.matchDbExp(Voucher.ID_PK_COLUMN, id)) 
                & Contact.COLLEGE.eq(college)
                & Voucher.STATUS.eq(ProductStatus.ACTIVE)
                & Voucher.EXPIRY_DATE.gt(new Date()).orExp(Voucher.EXPIRY_DATE.isNull())).selectOne(context)
        if (!voucher) {
            logger.error("Voucher is not available, id:$id collegeId: $college.id")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'Voucher is not available')).build())
        }
        voucher
    }
}
