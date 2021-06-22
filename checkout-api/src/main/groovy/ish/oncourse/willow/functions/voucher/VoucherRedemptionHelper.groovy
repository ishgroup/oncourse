package ish.oncourse.willow.functions.voucher

import ish.common.types.PaymentSource
import ish.common.types.PaymentStatus
import ish.common.types.PaymentType
import ish.common.types.ProductStatus
import ish.common.types.VoucherPaymentStatus
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.Invoice
import ish.oncourse.model.InvoiceLine
import ish.oncourse.model.PaymentIn
import ish.oncourse.model.PaymentInLine
import ish.oncourse.model.ProductItem
import ish.oncourse.model.Voucher
import ish.oncourse.model.VoucherPaymentIn
import ish.oncourse.model.VoucherProduct
import ish.oncourse.willow.model.common.CommonError
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.PersistenceState

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

class VoucherRedemptionHelper {

    private ObjectContext context

    private College college

    private Map<Voucher, Money> vouchers
    private Map<VoucherProduct, Money> voucherProducts
    private List<InvoiceLine> invoiceLines
    private Map<Invoice, Money> paymentPlan

    private Contact payer
    private Map<Voucher, PaymentIn> paymentMap

    VoucherRedemptionHelper(ObjectContext context, College college, Contact payer) {
        this.context = context
        this.vouchers = new HashMap<>()
        this.voucherProducts = new HashMap<>()
        this.invoiceLines = new ArrayList<>()
        this.paymentMap = new HashMap<>()
        this.paymentPlan = new HashMap<>()
        this.college = college
        this.payer = payer

    }

    /**
     * Add voucher to processing ensuring that it is not in use now.
     * @return if voucher has been accepted
     */
    boolean addVoucher(Voucher voucher, Money redeemNow) {
        if (!voucher.isInUse() && !voucher.isExpired() && !vouchers.keySet().contains(voucher)) {
            vouchers.put(voucher, redeemNow)
            return true
        }
        return false
    }

    void addPaymentPlan(Invoice invoice, Money payNow) {
        paymentPlan.put(invoice, payNow)
    }

    /**
     * Add list of invoice lines for processing.
     */
    void addInvoiceLines(Collection<InvoiceLine> invoiceLinesList) {
        this.invoiceLines.addAll(invoiceLinesList)
    }
    
    
    void createPaymentsForVouchers() {

        List<Voucher> vouchersList = Collections.list(Collections.enumeration(vouchers.keySet())) 
        List<InvoiceLine> invoiceLinesList = invoiceLines
        
        if (vouchersList.empty || invoiceLinesList.empty) {
            return
        }

        List<InvoiceLine> processedInvoiceLines = new ArrayList<>()
        
        processCourseVouchers(vouchersList, invoiceLinesList, processedInvoiceLines)
        invoiceLinesList.removeAll(processedInvoiceLines)
        
        processMoneyVouchers(vouchersList, invoiceLinesList)
        
    }

    private void processCourseVouchers(List<Voucher> vouchersList, List<InvoiceLine> invoiceLinesList, List<InvoiceLine> processedInvoiceLines) {
        if (vouchersList.isEmpty() || invoiceLinesList.isEmpty()) {
            throw new IllegalArgumentException("Either voucher or invoice line list is empty.")
        }
        
        List<InvoiceLine> enrolmentLines = invoiceLinesList.findAll { it.enrolment != null }
        List<Voucher> courseVouchers = vouchersList.findAll { it.voucherProduct.maxCoursesRedemption != null }

        if (enrolmentLines.empty || courseVouchers.empty) {
            return
        }

        // processing course vouchers
        for (Voucher voucher : courseVouchers) {
            for (InvoiceLine il : enrolmentLines) {
                if (!processedInvoiceLines.contains(il)) {
                    if (voucher.getVoucherProduct().getRedemptionCourses().contains(il.getEnrolment().getCourseClass().getCourse())) {
                        redeemVoucherForCourse(voucher, il)
                        processedInvoiceLines.add(il)
                    }
                }

                if (ProductStatus.REDEEMED.equals(voucher.getStatus())) {
                    // if current voucher is fully redeemed then skip to next one
                    break
                }
            }
        }
    }

    private void processMoneyVouchers(List<Voucher> vouchersList, List<InvoiceLine> invoiceLinesList) {
        if (vouchersList.empty) {
            throw new IllegalArgumentException("Either voucher or invoice line list is empty.")
        }
        // processing money vouchers
        // sort vouchers by remaining money value in ascending order to apply vouchers with less amount first
        List<Voucher> moneyVouchers = vouchersList.findAll { it.voucherProduct.maxCoursesRedemption == null }.sort { it.valueRemaining }
        if (moneyVouchers.empty || invoiceLinesList.empty) {
            return
        }

        LinkedList<Map.Entry<Invoice, Money>> queue = new LinkedList<>()

        Map<Invoice, Money> invoicesMap = new LinkedHashMap<>()
        invoiceLinesList.findAll { !isVoucherInvoiceLine(it) }.groupBy { it.invoice }.each {k,v ->
            Money leftToPay = paymentPlan.get(k)
            if (!leftToPay) {
                leftToPay = Money.ZERO
                v.each { leftToPay = leftToPay.add(it.priceTotalIncTax.subtract(it.discountTotalIncTax)) }
            }

            
            invoicesMap.put(k, leftToPay)
        }
        queue.addAll(invoicesMap.entrySet())


        Map.Entry<Invoice, Money> entry = queue.poll()
        if (entry) {
            Invoice invoice = entry.key
            Money leftToPay = entry.value


            for (Voucher voucher : moneyVouchers) {
                // redeem voucher against invoices while we can, then switch to next voucher
                while (!voucher.fullyRedeemed && vouchers.get(voucher).isGreaterThan(Money.ZERO)) {
                    if (!leftToPay.isGreaterThan(Money.ZERO)) {
                        if (queue.empty) {
                            // all paid...  finish processing
                            break
                        }
                        entry = queue.poll()
                        invoice = entry.key
                        leftToPay = entry.value

                    }
                    leftToPay = leftToPay.subtract(
                            redeemVoucherForMoney(voucher, invoice, leftToPay))
                }

            }
        } else {
            throw new BadRequestException(Response.status(400)
                    .entity(new CommonError(message: 'Impossible to pay for new vouchers by the existed voucher.'))
                    .build())
        }
    }


    private PaymentInLine redeemVoucherForCourse(Voucher voucher, InvoiceLine il) {
        if (!ProductStatus.ACTIVE.equals(voucher.getStatus()) || voucher.getClassesRemaining() < 1) {
            throw new IllegalStateException("Voucher is void.")
        }

        VoucherPaymentIn vp = getPaymentForVoucher(voucher, il)
        PaymentIn payment = vp.getPayment()

        payment.setAmount(payment.getAmount().add(il.getDiscountedPriceTotalIncTax()))

        PaymentInLine pil = getPaymentInLine(payment, il.invoice)
        pil.setAmount(pil.getAmount().add(il.getDiscountedPriceTotalIncTax()))

        voucher.setRedeemedCoursesCount(voucher.getRedeemedCoursesCount() + 1)

        if (voucher.isFullyRedeemed()) {
            voucher.setStatus(ProductStatus.REDEEMED)
        }

        return pil
    }

    private Money redeemVoucherForMoney(Voucher voucher, Invoice invoice, Money amountLeft) {
        if ((!ProductStatus.ACTIVE.equals(voucher.getStatus()) && PersistenceState.NEW != voucher.persistenceState) || voucher.getValueRemaining() == null) {
            throw new IllegalStateException("Voucher is void.")
        }

        Money redeemNow = vouchers.get(voucher).isLessThan(voucher.getValueRemaining()) ?
                vouchers.get(voucher) : voucher.getValueRemaining()

        Money amount = redeemNow.isLessThan(amountLeft) ? redeemNow : amountLeft

        VoucherPaymentIn vp = getPaymentForVoucher(voucher, null)
        PaymentIn payment = vp.getPayment()
        payment.setAmount(payment.getAmount().add(amount))

        PaymentInLine pil = getPaymentInLine(payment, invoice)

        pil.setAmount(pil.getAmount().add(amount))

        voucher.setRedemptionValue(voucher.getRedemptionValue().subtract(amount))

        if (voucher.isFullyRedeemed()) {
            voucher.setStatus(ProductStatus.REDEEMED)
        }

        // decrease amount which available for redemption from this voucher by amount just paid with it
        vouchers.put(voucher, vouchers.get(voucher).subtract(amount))

        return amount
    }

    private PaymentInLine getPaymentInLine(PaymentIn payment, Invoice invoice) {

        for (PaymentInLine paymentInLine : payment.getPaymentInLines()) {
            if (paymentInLine.getInvoice().equals(invoice)) {
                return paymentInLine
            }
        }

        PaymentInLine pil = context.newObject(PaymentInLine.class)
        pil.setCollege(college)
        pil.setPaymentIn(payment)
        pil.setInvoice(invoice)
        pil.setAmount(Money.ZERO)
        return pil
    }

    private VoucherPaymentIn getPaymentForVoucher(Voucher voucher, InvoiceLine il) {
        for (VoucherPaymentIn vp : voucher.getVoucherPaymentIns()) {
            if (vp.getPayment().getStatus() == PaymentStatus.IN_TRANSACTION &&
                    (il == null || il.equals(vp.getInvoiceLine()))) {
                return vp
            }
        }
        VoucherPaymentIn vp = context.newObject(VoucherPaymentIn.class)

        vp.setCollege(college)
        vp.setVoucher(voucher)
        vp.setInvoiceLine(il)
        vp.setStatus(VoucherPaymentStatus.APPROVED)

        PaymentIn payment = paymentMap.get(voucher)

        if (payment == null) {
            payment = context.newObject(PaymentIn.class)
            payment.setCollege(college)
            payment.setType(PaymentType.VOUCHER)
            payment.setContact(payer)
            payment.setAmount(Money.ZERO)
            payment.setSource(PaymentSource.SOURCE_WEB)
            payment.setStatus(PaymentStatus.IN_TRANSACTION)

            paymentMap.put(voucher, payment)
        }

        vp.setPayment(payment)
        return vp
    }

    private boolean isVoucherInvoiceLine(InvoiceLine invoiceLine) {
        List<ProductItem> productItems = invoiceLine.getProductItems()
        for (ProductItem productItem : productItems) {
            if (productItem instanceof Voucher)
                return true
        }
        return false
    }
}
