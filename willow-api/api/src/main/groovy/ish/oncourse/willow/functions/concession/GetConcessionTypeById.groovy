package ish.oncourse.willow.functions.concession

import ish.oncourse.model.College
import ish.oncourse.model.ConcessionType
import ish.oncourse.willow.model.common.CommonError
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

class GetConcessionTypeById {
    
    ObjectContext context
    College college
    final static  Logger logger = LoggerFactory.getLogger(GetConcessionTypeById.class)

    ConcessionType get(String id) {
        
        if (!id) {
            logger.error("concession type Id required")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'concessionTypeId required')).build())
        }

        ConcessionType concessionType =  (((ObjectSelect.query(ConcessionType)
                .where(ExpressionFactory.matchDbExp(ConcessionType.ID_PK_COLUMN, id))
                & ConcessionType.COLLEGE.eq(college)) & ConcessionType.IS_CONCESSION.eq(true))
                & ConcessionType.IS_ENABLED.eq(true))
                .selectOne(context)

        if (!concessionType) {
            logger.error("concession type  is not exist, id: $id")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: "concession type  is not exist, id: $id")).build())
        }
        concessionType
    }
    
}
