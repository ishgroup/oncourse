package ish.oncourse.willow.functions.promo

import ish.oncourse.model.College
import ish.oncourse.model.Discount
import ish.oncourse.willow.model.web.Promotion
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

class GetPromoByCode {
    
    ObjectContext context
    College college
    
    Promotion get(String code) {
        Discount discount  = (ObjectSelect.query(Discount).where(Discount.COLLEGE.eq(college))
                & Discount.CODE.eq(code)
                & Discount.getCurrentDateFilter()
                & Discount.IS_AVAILABLE_ON_WEB.eq(true)).selectFirst(context)
        if (discount) {
            return new Promotion(id: discount.id.toString(), name: discount.name, code: discount.code)
        }
        return null
    }
    
}
