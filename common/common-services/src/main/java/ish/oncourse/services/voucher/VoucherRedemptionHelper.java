/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.voucher;

import ish.common.types.*;
import ish.math.Money;
import ish.oncourse.model.*;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import java.util.*;

/**
 * Service class which processes vouchers and invoice lines added to it against each other and creates appropriate payments of voucher type for them if
 * applicable.
 *
 * @author dzmitry
 */
public class VoucherRedemptionHelper {

    private static final Expression COURSE_VOUCHER_QUALIFIER = ExpressionFactory.noMatchExp(Voucher.PRODUCT_PROPERTY + "." +
            VoucherProduct.MAX_COURSES_REDEMPTION_PROPERTY, null);
    private static final Expression MONEY_VOUCHER_QUALIFIER = ExpressionFactory.matchExp(Voucher.PRODUCT_PROPERTY + "." +
            VoucherProduct.MAX_COURSES_REDEMPTION_PROPERTY, null);

    private ObjectContext context;

    private College college;
    private Invoice invoice;

    private Map<Voucher, Money> vouchers;
    private List<InvoiceLine> invoiceLines;
    private List<Invoice> previousOwingInvoices;

    private Map<Voucher, Collection<InvoiceLine>> courseRedemptionMap;

    private Set<VoucherPaymentIn> voucherPayments;
    private Map<Voucher, PaymentIn> paymentMap;

    public VoucherRedemptionHelper(ObjectContext context, College college) {
        this.context = context;

        this.vouchers = new HashMap<Voucher, Money>();
        this.invoiceLines = new ArrayList<InvoiceLine>();
        this.previousOwingInvoices = new ArrayList<Invoice>();
        this.courseRedemptionMap = new HashMap<Voucher, Collection<InvoiceLine>>();
        this.voucherPayments = new HashSet<VoucherPaymentIn>();
        this.paymentMap = new HashMap<Voucher, PaymentIn>();
        this.college = college;
    }

    /**
     * Set invoice which will be linked to payments created.
     *
     * @param invoice
     */
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    /**
     * Get invoice used for processing.
     *
     * @return invoice
     */
    public Invoice getInvoice() {
        return invoice;
    }

    /**
     * Add voucher to processing ensuring that it is not in use now.
     *
     * @param voucher
     * @return if voucher has been accepted
     */
    public boolean addVoucher(Voucher voucher, Money redeemNow) {
        if (!voucher.isInUse() && !voucher.isExpired() && !vouchers.keySet().contains(voucher)) {
            vouchers.put(voucher, redeemNow);
            return true;
        }
        return false;
    }

    public void setCourseVoucherRedemptionList(Voucher voucher, Collection<InvoiceLine> invoiceLines) {
        courseRedemptionMap.put(voucher, invoiceLines);
    }

    public Collection<VoucherPaymentIn> getVoucherPayments() {
        return Collections.unmodifiableCollection(voucherPayments);
    }

    /**
     * Removes voucher from processing.
     *
     * @param voucher
     */
    public void removeVoucher(Voucher voucher) {
        this.vouchers.remove(voucher);
    }

    /**
     * Get all vouchers used in processing.
     *
     * @return vouchers processed
     */
    public List<Voucher> getVouchers() {
        return Collections.unmodifiableList(Collections.list(Collections.enumeration(vouchers.keySet())));
    }

    /**
     * Add list of invoice lines for processing.
     *
     * @param invoiceLinesList
     */
    public void addInvoiceLines(Collection<InvoiceLine> invoiceLinesList) {
        this.invoiceLines.addAll(invoiceLinesList);
    }

    public void addPreviousOwingInvoices(Collection<Invoice> invoices) {
        for (Invoice invoice : invoices) {

            // need to redeem vouchers only against previous owing invoices
            if (invoice.getAmountOwing().isGreaterThan(Money.ZERO)) {
                this.previousOwingInvoices.add(invoice);
            }
        }
    }

    /**
     * Returns list of payments assembled by the service.
     *
     * @return list of voucher payments
     */
    public List<PaymentIn> getPayments() {
        return Collections.unmodifiableList(new ArrayList<>(this.paymentMap.values()));
    }

    /**
     * Process currently added vouchers and invoice lines against each other creating payments.
     */
    public void processAgainstInvoices() {
        if (invoice != null) {
            createPaymentsForVouchers(Collections.list(Collections.enumeration(vouchers.keySet())), invoiceLines);
        }
    }

    /**
     * Discards all the changes made to voucher objects, i.e. created payments, copied new vouchers, status changes.
     */
    public void discardChanges() {

        for (Map.Entry<Voucher, PaymentIn> entry : paymentMap.entrySet()) {
            Voucher voucher = entry.getKey();
            PaymentIn payment = entry.getValue();

            List<VoucherPaymentIn> vps = new ArrayList<>(payment.getVoucherPaymentIns());
            for (VoucherPaymentIn vp : vps) {

                if (voucher.getVoucherProduct().getMaxCoursesRedemption() == null) {
                    voucher.setRedemptionValue(voucher.getRedemptionValue().add(payment.getAmount()));
                } else {
                    voucher.setRedeemedCoursesCount(voucher.getRedeemedCoursesCount() - 1);
                }
                if (!voucher.isFullyRedeemed()) {
                    voucher.setStatus(ProductStatus.ACTIVE);
                }

                context.deleteObjects(vp);
            }

            context.deleteObjects(payment.getPaymentInLines());
            context.deleteObjects(payment);
        }

        voucherPayments.clear();
        paymentMap.clear();
    }

    /**
     * Discards all model changes and removes all added invoices, invoice lines and vouchers.
     */
    public void clear() {
        discardChanges();
        invoiceLines.clear();
        previousOwingInvoices.clear();
        courseRedemptionMap.clear();
        vouchers.clear();
    }

    private void createPaymentsForVouchers(List<Voucher> vouchersList, List<InvoiceLine> invoiceLinesList) {
        if (!vouchersList.isEmpty()) {

            if (!invoiceLinesList.isEmpty()) {
                List<InvoiceLine> processedInvoiceLines = new ArrayList<InvoiceLine>();
                processCourseVouchers(vouchersList, invoiceLinesList, processedInvoiceLines);

                invoiceLinesList.removeAll(processedInvoiceLines);
            }

            processMoneyVouchers(vouchersList, invoiceLinesList);
        }
    }

    private void processCourseVouchers(List<Voucher> vouchersList, List<InvoiceLine> invoiceLinesList, List<InvoiceLine> processedInvoiceLines) {
        if (vouchersList.isEmpty() || invoiceLinesList.isEmpty()) {
            throw new IllegalArgumentException("Either voucher or invoice line list is empty.");
        }

        Expression enrolmentInvoiceLinesQual = ExpressionFactory.noMatchExp(InvoiceLine.ENROLMENT_PROPERTY, null);

        List<InvoiceLine> enrolmentLines = enrolmentInvoiceLinesQual.filterObjects(invoiceLinesList);

        if (!enrolmentLines.isEmpty()) {

            // processing course vouchers
            List<Voucher> courseVouchers = COURSE_VOUCHER_QUALIFIER.filterObjects(vouchersList);

            if (!courseVouchers.isEmpty()) {
                for (Voucher voucher : courseVouchers) {
                    for (InvoiceLine il : enrolmentLines) {
                        if (courseRedemptionMap.get(voucher) != null && courseRedemptionMap.get(voucher).contains(il))
                            if (!processedInvoiceLines.contains(il)) {
                                if (voucher.getVoucherProduct().getRedemptionCourses().contains(il.getEnrolment().getCourseClass().getCourse())) {
                                    redeemVoucherForCourse(voucher, il);
                                    processedInvoiceLines.add(il);
                                }
                            }

                        if (ProductStatus.REDEEMED.equals(voucher.getStatus())) {
                            // if current voucher is fully redeemed then skip to next one
                            break;
                        }
                    }
                }
            }
        }
    }

    private void processMoneyVouchers(List<Voucher> vouchersList, List<InvoiceLine> invoiceLinesList) {
        if (vouchersList.isEmpty()) {
            throw new IllegalArgumentException("Either voucher or invoice line list is empty.");
        }

        Money leftToPayForCurrentInvoice = Money.ZERO;

        for (InvoiceLine il : invoiceLinesList) {
            leftToPayForCurrentInvoice = leftToPayForCurrentInvoice
                    .add(il.getPriceTotalIncTax().subtract(il.getDiscountTotalIncTax()));
        }

        // processing money vouchers
        List<Voucher> moneyVouchers = MONEY_VOUCHER_QUALIFIER.filterObjects(vouchersList);

        // sort vouchers by remaining money value in ascending order to apply vouchers with less amount first
        Collections.sort(moneyVouchers, new Comparator<Voucher>() {

            @Override
            public int compare(Voucher o1, Voucher o2) {
                return o1.getValueRemaining().compareTo(o2.getValueRemaining());
            }

        });

        if (!moneyVouchers.isEmpty()) {

            // stack of previous invoices to conviniently retrieve next when current one is getting fully paid
            Deque<Invoice> previousOwing = new ArrayDeque<>(previousOwingInvoices);

            Invoice currentInvoice = invoice;

            for (Voucher voucher : moneyVouchers) {

                // redeem voucher against invoices while we can, then switch to next voucher
                while (!voucher.isFullyRedeemed() && vouchers.get(voucher).isGreaterThan(Money.ZERO)) {

                    if (!leftToPayForCurrentInvoice.isGreaterThan(Money.ZERO)) {

                        if (previousOwing.isEmpty()) {
                            // all paid...  finish processing
                            break;
                        }

                        currentInvoice = previousOwing.pop();
                        leftToPayForCurrentInvoice = currentInvoice.getAmountOwing();
                    }

                    leftToPayForCurrentInvoice = leftToPayForCurrentInvoice.subtract(
                            redeemVoucherForMoney(voucher, currentInvoice, leftToPayForCurrentInvoice));
                }
            }
        }
    }

    private PaymentInLine redeemVoucherForCourse(Voucher voucher, InvoiceLine il) {
        if (!ProductStatus.ACTIVE.equals(voucher.getStatus()) || voucher.getClassesRemaining() < 1) {
            throw new IllegalStateException("Voucher is void.");
        }

        VoucherPaymentIn vp = getPaymentForVoucher(voucher, il, true);
        PaymentIn payment = vp.getPayment();

        payment.setAmount(payment.getAmount().add(il.getDiscountedPriceTotalIncTax()));

        PaymentInLine pil = createPaymentInLine(payment, invoice);
        pil.setAmount(pil.getAmount().add(il.getDiscountedPriceTotalIncTax()));

        ///todo we can use use

        //pil.setAccountOut(invoice.getDebtorsAccount());

        voucher.setRedeemedCoursesCount(voucher.getRedeemedCoursesCount() + 1);

        if (voucher.isFullyRedeemed()) {
            voucher.setStatus(ProductStatus.REDEEMED);
        }

        return pil;
    }

    private Money redeemVoucherForMoney(Voucher voucher, Invoice invoice, Money amountLeft) {
        if (!ProductStatus.ACTIVE.equals(voucher.getStatus()) || voucher.getValueRemaining() == null) {
            throw new IllegalStateException("Voucher is void.");
        }

        Money redeemNow = vouchers.get(voucher).isLessThan(voucher.getValueRemaining()) ?
                vouchers.get(voucher) : voucher.getValueRemaining();

        Money amount = redeemNow.isLessThan(amountLeft) ? redeemNow : amountLeft;

        VoucherPaymentIn vp = getPaymentForVoucher(voucher, null, true);
        PaymentIn payment = vp.getPayment();
        payment.setAmount(payment.getAmount().add(amount));

        PaymentInLine pil = createPaymentInLine(payment, invoice);

        pil.setAmount(pil.getAmount().add(amount));
        //pil.setAccountOut(invoice.getDebtorsAccount());


        voucher.setRedemptionValue(voucher.getRedemptionValue().subtract(amount));

        if (voucher.isFullyRedeemed()) {
            voucher.setStatus(ProductStatus.REDEEMED);
        }

        // decrease amount which available for redemption from this voucher by amount just paid with it
        vouchers.put(voucher, vouchers.get(voucher).subtract(amount));

        return amount;
    }

    private PaymentInLine createPaymentInLine(PaymentIn payment, Invoice invoice) {
        PaymentInLine pil = context.newObject(PaymentInLine.class);
        pil.setCollege(college);
        pil.setPaymentIn(payment);
        pil.setInvoice(invoice);
        pil.setAmount(Money.ZERO);
        return pil;
    }

    private VoucherPaymentIn getPaymentForVoucher(Voucher voucher, InvoiceLine il, boolean create) {
        for (VoucherPaymentIn vp : voucher.getVoucherPaymentIns()) {
            if (vp.getPayment().getStatus() == PaymentStatus.NEW &&
                    (il == null || il.equals(vp.getInvoiceLine()))) {
                return vp;
            }
        }
        if (create) {
            VoucherPaymentIn vp = context.newObject(VoucherPaymentIn.class);

            vp.setCollege(college);
            vp.setVoucher(voucher);
            vp.setInvoiceLine(il);
            vp.setStatus(VoucherPaymentStatus.APPROVED);

            PaymentIn payment = paymentMap.get(voucher);

            if (payment == null) {
                payment = context.newObject(PaymentIn.class);
                payment.setCollege(college);
                payment.setType(PaymentType.VOUCHER);
                payment.setContact(invoice.getContact());
                payment.setAmount(Money.ZERO);
                //payment.setAccount(vp.getVoucher().getVoucherProduct().getLiabilityAccount());
                payment.setSource(PaymentSource.SOURCE_WEB);

                paymentMap.put(voucher, payment);
            }

            vp.setPayment(payment);

            voucherPayments.add(vp);
            return vp;
        }
        return null;
    }

    public College getCollege() {
        return college;
    }
}
