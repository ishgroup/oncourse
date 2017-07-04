package ish.oncourse.willow.model.checkout;


public class Amount  {
  
    private Double owing = null;
    private Double total = null;
    private Double discount = null;
    private Double payNow = null;

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


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Amount {\n");
      
      sb.append("    owing: ").append(toIndentedString(owing)).append("\n");
      sb.append("    total: ").append(toIndentedString(total)).append("\n");
      sb.append("    discount: ").append(toIndentedString(discount)).append("\n");
      sb.append("    payNow: ").append(toIndentedString(payNow)).append("\n");
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

