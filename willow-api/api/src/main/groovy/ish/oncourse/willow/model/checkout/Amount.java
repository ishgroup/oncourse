package ish.oncourse.willow.model.checkout;

import ish.oncourse.willow.model.checkout.VoucherPayment;
import java.util.ArrayList;
import java.util.List;

public class Amount  {
  
    private Double owing = null;
    private Double total = null;
    private Double discount = null;
    private Double payNow = null;
    private Double minPayNow = null;
    private Boolean isEditable = null;
    private List<VoucherPayment> voucherPayments = new ArrayList<VoucherPayment>();

    /**
     * Get owing
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

    /**
     * Get total
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
     * Get discount
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
     * Get payNow
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
     * Get minPayNow
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
     * Get isEditable
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
     * Get voucherPayments
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


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Amount {\n");
      
      sb.append("    owing: ").append(toIndentedString(owing)).append("\n");
      sb.append("    total: ").append(toIndentedString(total)).append("\n");
      sb.append("    discount: ").append(toIndentedString(discount)).append("\n");
      sb.append("    payNow: ").append(toIndentedString(payNow)).append("\n");
      sb.append("    minPayNow: ").append(toIndentedString(minPayNow)).append("\n");
      sb.append("    isEditable: ").append(toIndentedString(isEditable)).append("\n");
      sb.append("    voucherPayments: ").append(toIndentedString(voucherPayments)).append("\n");
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

