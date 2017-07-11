package ish.oncourse.willow.functions.promo

import ish.common.types.ProductStatus
import ish.oncourse.model.College
import ish.oncourse.model.Voucher
import ish.oncourse.willow.model.checkout.RedeemVoucher
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.model.web.Contact
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect



class GetVoucherByCode {

    ObjectContext context
    College college
    CommonError error

    RedeemVoucher redeemVoucher

    GetVoucherByCode get(String code) {
        Voucher voucher = (((ObjectSelect.query(Voucher).where(Voucher.COLLEGE.eq(college))
                & Voucher.CODE.eq(code)) & Voucher.STATUS.eq(ProductStatus.ACTIVE)) & Voucher.EXPIRY_DATE.gt(new Date()).orExp(Voucher.EXPIRY_DATE.isNull()))
                .selectFirst(context)
        
        if (voucher.inUse) {
            error = new CommonError(message: 'Selected voucher cannot be added right now since it is currently being used in another payment process. Please try again later.')
        } else if (voucher) {
            redeemVoucher = new RedeemVoucher().with { v ->
                v.name = voucher.product.name
                v.id = voucher.id.toString()
                v.code = code
                if (voucher.contact) {
                    v.payer = new Contact().with { c ->
                        c.id = voucher.contact.id.toString()
                        c.firstName = voucher.contact.givenName
                        c.lastName = voucher.contact.familyName
                        c.company = voucher.contact.isCompany
                        c
                    }
                }
                v
            }
        }
        
        return this
        
    } 
    
}
