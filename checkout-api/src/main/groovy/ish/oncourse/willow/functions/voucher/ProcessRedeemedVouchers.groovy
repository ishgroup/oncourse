package ish.oncourse.willow.functions.voucher

import ish.common.types.ProductStatus
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.Product
import ish.oncourse.model.Voucher
import ish.oncourse.model.VoucherProduct
import ish.oncourse.willow.checkout.functions.EnrolmentNode
import ish.oncourse.willow.checkout.functions.GetProduct
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.VoucherPayment
import ish.oncourse.willow.model.common.CommonError
import org.apache.cayenne.ObjectContext

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

class ProcessRedeemedVouchers {

    private ObjectContext context
    private College college
    private CheckoutModelRequest checkoutModelRequest
    private List<EnrolmentNode> enrolmentNodes

    private List<Voucher> vouchers
    private Map<VoucherProduct, ish.oncourse.willow.model.checkout.Voucher> purchasedVouchersWithProducts

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
        if (checkoutModelRequest.redeemedVoucherIds.empty && checkoutModelRequest.redeemedVoucherProductIds.empty) {
            return this
        }
        vouchers = checkoutModelRequest.redeemedVoucherIds.collect { id -> new GetVoucher(context, college, id).get() }
        purchasedVouchersWithProducts = fillPurchasedVouchersWithProducts()

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
        Map<VoucherProduct, ish.oncourse.willow.model.checkout.Voucher> moneyPurchasedVouchers = purchasedVouchersWithProducts
                .findAll { it.key.maxCoursesRedemption == null }
                .sort { it.key.value != null ? it.key.value : it.key.priceExTax }

        moneyPurchasedVouchers.each {purchasedVoucher ->
            ish.oncourse.willow.model.checkout.Voucher buyingVoucher = purchasedVoucher.value
            for (int i = 0; i < buyingVoucher.quantity && leftToPay.isGreaterThan(Money.ZERO); i++) {
                VoucherPayment payment = new VoucherPayment(redeemVoucherProductId: purchasedVoucher.key.id.toString(), name: purchasedVoucher.key.name)
                applyPayment(payment, Money.valueOf(buyingVoucher.value.toBigDecimal()))
            }
        }

        moneyVouchers.each { voucher ->
            VoucherPayment payment = new VoucherPayment(redeemVoucherId: voucher.id.toString(), name: voucher.voucherProduct.name)
            applyPayment(payment, voucher.valueRemaining)
        }
    }

    private void applyPayment(VoucherPayment payment, Money availableValue) {
        Money amount = Money.ZERO

        if (leftToPay.isGreaterThan(Money.ZERO)) {
            amount = amount.add(availableValue.min(leftToPay))
            leftToPay = leftToPay.subtract(amount)
        }

        payment.amount = amount.doubleValue()
        vouchersTotal = vouchersTotal.add(amount)

        voucherPayments << payment
    }

    private Map<VoucherProduct, ish.oncourse.willow.model.checkout.Voucher> fillPurchasedVouchersWithProducts() {
        Map<VoucherProduct, ish.oncourse.willow.model.checkout.Voucher> purchasedVouchersWithProducts = [:]

        List<ish.oncourse.willow.model.checkout.Voucher> purchasedVouchers = checkoutModelRequest.contactNodes*.vouchers.flatten() as List<ish.oncourse.willow.model.checkout.Voucher>
        checkoutModelRequest.redeemedVoucherProductIds.each {voucherProductId ->
            Product voucherProduct = new GetProduct(context, college, voucherProductId).get()
            if (!(voucherProduct instanceof VoucherProduct)) {
                throw new BadRequestException(Response.status(400)
                        .entity(new CommonError(message: 'Specify a voucher product id which should be redeemed.'))
                        .build())
            }

            ish.oncourse.willow.model.checkout.Voucher purchasedVoucher = purchasedVouchers.find {voucherProductId == it.productId }
            if (!purchasedVoucher) {
                throw new BadRequestException(Response.status(400)
                        .entity(new CommonError(message: 'The specified voucher product id is not being purchased.'))
                        .build())
            }

            purchasedVouchersWithProducts.put(voucherProduct as VoucherProduct, purchasedVoucher)
        }

        return purchasedVouchersWithProducts
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
