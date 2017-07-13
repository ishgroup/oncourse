package ish.oncourse.willow.functions.voucher

import ish.common.types.ProductStatus
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.Voucher
import ish.oncourse.willow.checkout.functions.EnrolmentNode
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.VoucherPayment
import ish.oncourse.willow.model.common.CommonError
import org.apache.cayenne.ObjectContext

class ProcessRedeemedVouchers {

    private ObjectContext context
    private College college
    private CheckoutModelRequest checkoutModelRequest
    private List<EnrolmentNode> enrolmentNodes

    private List<Voucher> vouchers

    Money leftToPay = Money.ZERO
    Money vouchersTotal = Money.ZERO
    CommonError error
    List<VoucherPayment> voucherPayments = []

    ProcessRedeemedVouchers(ObjectContext context, College college, CheckoutModelRequest checkoutModelRequest, Money leftToPay, List<EnrolmentNode> enrolmentNodes) {
        this.context = context
        this.college = college
        this.checkoutModelRequest = checkoutModelRequest
        this.leftToPay = this.leftToPay.add(leftToPay)
        this.enrolmentNodes = enrolmentNodes
    }


    ProcessRedeemedVouchers process() {
        if (checkoutModelRequest.redeemedVoucherIds.empty) {
            return this
        }
        vouchers = checkoutModelRequest.redeemedVoucherIds.collect { id -> new GetVoucher(context, college, id).get() }

        if (validate()) {
            processCourseVouchers()
            processMoneyVouchers()
        }
        context.rollbackChanges()
        return this
    }
    
    private void processCourseVouchers() {
        List<Voucher> courseVouchers = vouchers.findAll { it.voucherProduct.maxCoursesRedemption != null }
        List<EnrolmentNode> processedNodes = []
        
        courseVouchers.each { voucher ->
            
            if (ProductStatus.ACTIVE != voucher.status || voucher.classesRemaining < 1) {
                error = new CommonError(message: "Voucher $voucher.product.name is void.")
                return 
            }
            
            VoucherPayment payment = new VoucherPayment(redeemVoucherId: voucher.id.toString(), name: voucher.voucherProduct.name)
            Money amount = Money.ZERO
            
            List<EnrolmentNode> unprocessedNodes = enrolmentNodes.findAll { !processedNodes.contains(it) && voucher.voucherProduct.redemptionCourses.contains(it.course) }
            
            for (EnrolmentNode node : unprocessedNodes) {
                
                amount = amount.add(node.finalPrice)
                leftToPay = leftToPay.subtract(node.payNow)
                processedNodes << node
                voucher.redeemedCoursesCount = voucher.redeemedCoursesCount + 1

                if (voucher.fullyRedeemed) {
                    break
                }
                
            }
            payment.amount = amount.doubleValue()
            vouchersTotal = vouchersTotal.add(amount)

            voucherPayments << payment
        }
    }

    private void processMoneyVouchers() {
        List<Voucher> moneyVouchers = vouchers.findAll { it.voucherProduct.maxCoursesRedemption == null }.sort { it.valueRemaining }

        moneyVouchers.each { voucher ->
            VoucherPayment payment = new VoucherPayment(redeemVoucherId: voucher.id.toString(), name: voucher.voucherProduct.name)
            Money amount = Money.ZERO
            
            if (leftToPay.isGreaterThan(Money.ZERO)) {
                amount = amount.add(voucher.valueRemaining.min(leftToPay))
                leftToPay = leftToPay.subtract(amount)
            }
            
            payment.amount = amount.doubleValue()
            vouchersTotal = vouchersTotal.add(amount)

            voucherPayments << payment
        }
    }

    private boolean validate() {
        List<Contact> payers = vouchers.findAll { it.contact != null }.collect { it.contact }.unique()

        if (payers.size() > 1) {
            error = new CommonError(message: 'Redeemed vouchers should has single payer')
            return false
        } else if (payers.size() == 1 && payers[0].id.toString() != checkoutModelRequest.payerId) {
            error = new CommonError(message: 'Payer is wrong')
            return false
        }

        return true
    }
    
}
