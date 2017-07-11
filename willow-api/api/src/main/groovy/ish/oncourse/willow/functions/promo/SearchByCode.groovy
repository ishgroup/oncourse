package ish.oncourse.willow.functions.promo

import ish.oncourse.model.College
import ish.oncourse.willow.model.checkout.CodeResponse
import ish.oncourse.willow.model.common.CommonError
import org.apache.cayenne.ObjectContext

class SearchByCode {
    ObjectContext context
    College college
    CodeResponse response = new CodeResponse()

    CommonError error
    
    SearchByCode get(String code) {
        response.promotion = new GetPromoByCode(context: context, college: college).get(code)
        if (!response.promotion) {
            GetVoucherByCode voucherByCode = new GetVoucherByCode(context: context, college: college).get(code)
            error = voucherByCode.error
            response.voucher = voucherByCode.redeemVoucher
        }
        this
    }
}
