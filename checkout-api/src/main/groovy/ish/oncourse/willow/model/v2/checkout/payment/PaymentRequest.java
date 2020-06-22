package ish.oncourse.willow.model.v2.checkout.payment;


public class PaymentRequest  {
  
    private Long payerId = null;
    private String merchantReference = null;
    private String sessionId = null;
    private Double ccAmount = null;

    /**
     * Get payerId
     * @return payerId
     */
    public Long getPayerId() {
        return payerId;
    }

    public void setPayerId(Long payerId) {
       this.payerId = payerId;
    }

    public PaymentRequest payerId(Long payerId) {
      this.payerId = payerId;
      return this;
    }

    /**
     * Get merchantReference
     * @return merchantReference
     */
    public String getMerchantReference() {
        return merchantReference;
    }

    public void setMerchantReference(String merchantReference) {
       this.merchantReference = merchantReference;
    }

    public PaymentRequest merchantReference(String merchantReference) {
      this.merchantReference = merchantReference;
      return this;
    }

    /**
     * String length 16 of hexadecimal digits
     * @return sessionId
     */
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
       this.sessionId = sessionId;
    }

    public PaymentRequest sessionId(String sessionId) {
      this.sessionId = sessionId;
      return this;
    }

    /**
     * Amount allocated for credit card payment
     * @return ccAmount
     */
    public Double getCcAmount() {
        return ccAmount;
    }

    public void setCcAmount(Double ccAmount) {
       this.ccAmount = ccAmount;
    }

    public PaymentRequest ccAmount(Double ccAmount) {
      this.ccAmount = ccAmount;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class PaymentRequest {\n");
      
      sb.append("    payerId: ").append(toIndentedString(payerId)).append("\n");
      sb.append("    merchantReference: ").append(toIndentedString(merchantReference)).append("\n");
      sb.append("    sessionId: ").append(toIndentedString(sessionId)).append("\n");
      sb.append("    ccAmount: ").append(toIndentedString(ccAmount)).append("\n");
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

