package ish.oncourse.willow.model.checkout;


public class Amount  {
  
    private String owing = null;
    private String total = null;
    private String discount = null;
    private String payNow = null;

    /**
     * Get owing
     * @return owing
     */
    public String getOwing() {
        return owing;
    }

    public void setOwing(String owing) {
       this.owing = owing;
    }

    public Amount owing(String owing) {
      this.owing = owing;
      return this;
    }

    /**
     * Get total
     * @return total
     */
    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
       this.total = total;
    }

    public Amount total(String total) {
      this.total = total;
      return this;
    }

    /**
     * Get discount
     * @return discount
     */
    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
       this.discount = discount;
    }

    public Amount discount(String discount) {
      this.discount = discount;
      return this;
    }

    /**
     * Get payNow
     * @return payNow
     */
    public String getPayNow() {
        return payNow;
    }

    public void setPayNow(String payNow) {
       this.payNow = payNow;
    }

    public Amount payNow(String payNow) {
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

