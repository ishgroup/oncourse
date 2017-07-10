package ish.oncourse.willow.functions.promo

import ish.oncourse.model.College
import ish.oncourse.willow.model.checkout.CodeResponse
import org.apache.cayenne.ObjectContext

class SearchByCode {
    ObjectContext context
    College college
    CodeResponse response = new CodeResponse()
    
    CodeResponse get(String code) {
        response.promotion = new GetPromoByCode(context: context, college: college).get(code)
        if (!response.promotion) {
            response.voucher = new GetVoucherByCode(context: context, college: college).get(code)
        }
        response
    }
}
