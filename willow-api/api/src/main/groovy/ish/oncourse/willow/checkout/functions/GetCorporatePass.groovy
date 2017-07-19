package ish.oncourse.willow.checkout.functions

import ish.oncourse.model.College
import ish.oncourse.model.CorporatePass
import ish.oncourse.willow.model.common.CommonError
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

class GetCorporatePass extends Get<CorporatePass> {

    final static  Logger logger = LoggerFactory.getLogger(GetContact.class)

    GetCorporatePass(ObjectContext context, College college, String id) {
        super(context, college, id)
    }

    CorporatePass get() {
        if (!StringUtils.trimToNull(id)) {
            logger.error("classId required")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'corporatePassId required')).build())
        }

        CorporatePass pass = (ObjectSelect.query(CorporatePass).where(ExpressionFactory.matchDbExp(CorporatePass.ID_PK_COLUMN, id)) 
                & CorporatePass.COLLEGE.eq(college)
                & (CorporatePass.EXPIRY_DATE.gte(new Date()).orExp(CorporatePass.EXPIRY_DATE.isNull())))
                .selectOne(context)
        if (!pass) {
            logger.error("Corporate Pass is not valid or has expired, corporatePassId: $id, collegeId: $college.id")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'This code is not valid or has expired. Please contact the college.')).build())
        }
        pass
    }
}
