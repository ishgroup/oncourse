package ish.oncourse.willow.functions.voucher

import ish.oncourse.model.InvoiceLine
import ish.oncourse.model.ProductItem
import ish.oncourse.model.Voucher
import ish.oncourse.willow.model.checkout.VoucherPayment
import ish.oncourse.willow.model.common.CommonError

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

class GetPurchasedVouchersInCurrentTransaction {

    List<VoucherPayment> voucherProductPayments
    List<InvoiceLine> invoiceLines
    List<Voucher> purchasedVouchers

    GetPurchasedVouchersInCurrentTransaction(List<VoucherPayment> voucherProductPayments, List<InvoiceLine> invoiceLines) {
        this.voucherProductPayments = voucherProductPayments
        this.invoiceLines = invoiceLines
        this.purchasedVouchers = []
    }

    List<Voucher> get() {
        if (!voucherProductPayments.empty) {
            if (invoiceLines.empty) {
                throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'Voucher should be added to checkout.')).build())
            }
            List<ProductItem> items = invoiceLines*.productItems.flatten() as ArrayList<ProductItem>

            for (VoucherPayment payment : voucherProductPayments) {
                for (ProductItem item : items) {
                    if (Long.valueOf(payment.redeemVoucherProductId) == item.product.id) {
                        if (!purchasedVouchers.contains(item as Voucher)) {
                            purchasedVouchers << (item as Voucher)
                            break
                        }
                    }
                }
            }

            if (purchasedVouchers.empty) {
                throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'There are not found vouchers which are being bought in that transaction.')).build())
            }
        }
        return purchasedVouchers
    }
}
