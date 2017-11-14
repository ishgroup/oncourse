package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.willow.functions.promo.GetPromoByCode
import ish.oncourse.willow.functions.promo.SearchByCode
import ish.oncourse.willow.model.checkout.CodeResponse
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.model.web.Promotion
import ish.oncourse.willow.service.PromotionApi

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
        Promotion response = new GetPromoByCode(context: cayenneService.newContext(), college: collegeService.college).get(code)
        
        if (response) {
            response
        } else {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity(new CommonError(message: 'The code you have entered was incorrect or not available.')).build())
        }
    }

    @Override
    CodeResponse submitCode(String code) {
        SearchByCode searchByCode = new SearchByCode(context: cayenneService.newContext(), college: collegeService.college).get(code)

        if (searchByCode.error) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity(searchByCode.error).build())
        } else {
            searchByCode.response
        }
    }
}

