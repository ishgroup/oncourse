package ish.oncourse.willow.model.checkout;

import ish.oncourse.willow.model.checkout.VoucherPayment;
import java.util.ArrayList;
import java.util.List;

public class Amount  {
  
    private Double total = null;
    private Double discount = null;
    private Double subTotal = null;
    private Double minPayNow = null;
    private Double payNow = null;
    private Boolean isEditable = null;
    private List<VoucherPayment> voucherPayments = new ArrayList<VoucherPayment>();
    private Double credit = null;
    private Double ccPayment = null;
    private Double owing = null;

    /**
     * total price excluding discounts
     * @return total
     */
    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
       this.total = total;
    }

    public Amount total(Double total) {
      this.total = total;
      return this;
    }

    /**
     * total discount amount
     * @return discount
     */
    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
       this.discount = discount;
    }

    public Amount discount(Double discount) {
      this.discount = discount;
      return this;
    }

    /**
     * total price including discounts (discounted price)
     * @return subTotal
     */
    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
       this.subTotal = subTotal;
    }

    public Amount subTotal(Double subTotal) {
      this.subTotal = subTotal;
      return this;
    }

    /**
     * min amount that user need to pay for invoice/invoices (can be less than subTotal if payment plan invoices precented)
     * @return minPayNow
     */
    public Double getMinPayNow() {
        return minPayNow;
    }

    public void setMinPayNow(Double minPayNow) {
       this.minPayNow = minPayNow;
    }

    public Amount minPayNow(Double minPayNow) {
      this.minPayNow = minPayNow;
      return this;
    }

    /**
     * actual amount allocated for invoice/invoices (equalse subTotal if payment plan invoices not presented)
     * @return payNow
     */
    public Double getPayNow() {
        return payNow;
    }

    public void setPayNow(Double payNow) {
       this.payNow = payNow;
    }

    public Amount payNow(Double payNow) {
      this.payNow = payNow;
      return this;
    }

    /**
     * true if minPayNow less that subTotal
     * @return isEditable
     */
    public Boolean getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(Boolean isEditable) {
       this.isEditable = isEditable;
    }

    public Amount isEditable(Boolean isEditable) {
      this.isEditable = isEditable;
      return this;
    }

    /**
     * voucher payments list - appears if redeem any kind of vouchers
     * @return voucherPayments
     */
    public List<VoucherPayment> getVoucherPayments() {
        return voucherPayments;
    }

    public void setVoucherPayments(List<VoucherPayment> voucherPayments) {
       this.voucherPayments = voucherPayments;
    }

    public Amount voucherPayments(List<VoucherPayment> voucherPayments) {
      this.voucherPayments = voucherPayments;
      return this;
    }

    public Amount addVoucherPaymentsItem(VoucherPayment voucherPaymentsItem) {
      this.voucherPayments.add(voucherPaymentsItem);
      return this;
    }

    /**
     * credit amount that will be used to pay for current invoice/invoices (students credit notes)
     * @return credit
     */
    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
       this.credit = credit;
    }

    public Amount credit(Double credit) {
      this.credit = credit;
      return this;
    }

    /**
     * amount paid by card
     * @return ccPayment
     */
    public Double getCcPayment() {
        return ccPayment;
    }

    public void setCcPayment(Double ccPayment) {
       this.ccPayment = ccPayment;
    }

    public Amount ccPayment(Double ccPayment) {
      this.ccPayment = ccPayment;
      return this;
    }

    /**
     * outstanding owing amount
     * @return owing
     */
    public Double getOwing() {
        return owing;
    }

    public void setOwing(Double owing) {
       this.owing = owing;
    }

    public Amount owing(Double owing) {
      this.owing = owing;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Amount {\n");
      
      sb.append("    total: ").append(toIndentedString(total)).append("\n");
      sb.append("    discount: ").append(toIndentedString(discount)).append("\n");
      sb.append("    subTotal: ").append(toIndentedString(subTotal)).append("\n");
      sb.append("    minPayNow: ").append(toIndentedString(minPayNow)).append("\n");
      sb.append("    payNow: ").append(toIndentedString(payNow)).append("\n");
      sb.append("    isEditable: ").append(toIndentedString(isEditable)).append("\n");
      sb.append("    voucherPayments: ").append(toIndentedString(voucherPayments)).append("\n");
      sb.append("    credit: ").append(toIndentedString(credit)).append("\n");
      sb.append("    ccPayment: ").append(toIndentedString(ccPayment)).append("\n");
      sb.append("    owing: ").append(toIndentedString(owing)).append("\n");
      sb.append("}");
      return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private static String toIndentedString(java.lang.Object o) {
      if (o == null) {
        return "null";
      }
      return o.toString().replace("\n", "\n    ");
    }
}

