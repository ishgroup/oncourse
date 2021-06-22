package ish.oncourse.willow.model.checkout;


public class VoucherPayment  {
  
    private String redeemVoucherProductId = null;
    private String redeemVoucherId = null;
    private String name = null;
    private Double amount = null;

    /**
     * Get redeemVoucherProductId
     * @return redeemVoucherProductId
     */
    public String getRedeemVoucherProductId() {
        return redeemVoucherProductId;
    }

    public void setRedeemVoucherProductId(String redeemVoucherProductId) {
       this.redeemVoucherProductId = redeemVoucherProductId;
    }

    public VoucherPayment redeemVoucherProductId(String redeemVoucherProductId) {
      this.redeemVoucherProductId = redeemVoucherProductId;
      return this;
    }

    /**
     * Get redeemVoucherId
     * @return redeemVoucherId
     */
    public String getRedeemVoucherId() {
        return redeemVoucherId;
    }

    public void setRedeemVoucherId(String redeemVoucherId) {
       this.redeemVoucherId = redeemVoucherId;
    }

    public VoucherPayment redeemVoucherId(String redeemVoucherId) {
      this.redeemVoucherId = redeemVoucherId;
      return this;
    }

    /**
     * Get name
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
       this.name = name;
    }

    public VoucherPayment name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Get amount
     * @return amount
     */
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
       this.amount = amount;
    }

    public VoucherPayment amount(Double amount) {
      this.amount = amount;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class VoucherPayment {\n");
      
      sb.append("    redeemVoucherProductId: ").append(toIndentedString(redeemVoucherProductId)).append("\n");
      sb.append("    redeemVoucherId: ").append(toIndentedString(redeemVoucherId)).append("\n");
      sb.append("    name: ").append(toIndentedString(name)).append("\n");
      sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
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

