package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.model.Discount
import ish.oncourse.willow.cayenne.CayenneService
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.model.web.Promotion
import ish.oncourse.willow.service.PromotionApi
import org.apache.cayenne.query.ObjectSelect

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

@CompileStatic
class PromotionApiServiceImpl implements PromotionApi {

    private CayenneService cayenneService
    private CollegeService collegeService

    @Inject
    PromotionApiServiceImpl(CayenneService cayenneService, CollegeService collegeService) {
        this.collegeService = collegeService
        this.cayenneService = cayenneService
    }
    
    @Override
    Promotion getPromotion(String code) {
        
     Discount discount  = (ObjectSelect.query(Discount).where(Discount.COLLEGE.eq(collegeService.college)) 
        & Discount.CODE.eq(code)
        & Discount.getCurrentDateFilter()
        & Discount.IS_AVAILABLE_ON_WEB.eq(true)).selectFirst(cayenneService.newContext())
        
        if (discount) {
            new Promotion(id: discount.id.toString(), name: discount.name, code: discount.code)
        } else {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity(new CommonError(message: 'The code you have entered was incorrect or not available.')).build())
        }
        
    }

}

