package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.model.Discount
import ish.oncourse.willow.model.Promotion
import ish.oncourse.willow.service.PromotionApi
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.cayenne.query.ObjectSelect

import javax.ws.rs.PathParam


@CompileStatic
class PromotionApiServiceImpl implements PromotionApi {

    private ServerRuntime cayenneRuntime

    @Inject
    PromotionApiServiceImpl(ServerRuntime cayenneRuntime) {
        this.cayenneRuntime = cayenneRuntime
    }
    
    @Override
    Promotion getPromotion(@PathParam("code") String code) {
        
     Discount discount  = (ObjectSelect.query(Discount).where(Discount.COLLEGE.isNotNull()) 
        & Discount.CODE.eq(code)
        & Discount.getCurrentDateFilter()
        & Discount.IS_AVAILABLE_ON_WEB.eq(true)).selectFirst(cayenneRuntime.newContext())
        
        discount ? new Promotion(id: discount.id.toString(), name: discount.name, code: discount.code) : null
    }

}

