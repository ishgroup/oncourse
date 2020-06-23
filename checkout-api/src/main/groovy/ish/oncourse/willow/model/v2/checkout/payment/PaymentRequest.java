package ish.oncourse.willow.model.v2.checkout.payment;

import ish.oncourse.willow.model.checkout.CheckoutModelRequest;

public class PaymentRequest  {
  
    private CheckoutModelRequest checkoutModelRequest = null;
    private String merchantReference = null;
    private String sessionId = null;
    private Double ccAmount = null;
    private Boolean storeCard = null;

    /**
     * Get checkoutModelRequest
     * @return checkoutModelRequest
     */
    public CheckoutModelRequest getCheckoutModelRequest() {
        return checkoutModelRequest;
    }

    public void setCheckoutModelRequest(CheckoutModelRequest checkoutModelRequest) {
       this.checkoutModelRequest = checkoutModelRequest;
    }

    public PaymentRequest checkoutModelRequest(CheckoutModelRequest checkoutModelRequest) {
      this.checkoutModelRequest = checkoutModelRequest;
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

    /**
     * save CC details to proc
     * @return storeCard
     */
    public Boolean getStoreCard() {
        return storeCard;
    }

    public void setStoreCard(Boolean storeCard) {
       this.storeCard = storeCard;
    }

    public PaymentRequest storeCard(Boolean storeCard) {
      this.storeCard = storeCard;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class PaymentRequest {\n");
      
      sb.append("    checkoutModelRequest: ").append(toIndentedString(checkoutModelRequest)).append("\n");
      sb.append("    merchantReference: ").append(toIndentedString(merchantReference)).append("\n");
      sb.append("    sessionId: ").append(toIndentedString(sessionId)).append("\n");
      sb.append("    ccAmount: ").append(toIndentedString(ccAmount)).append("\n");
      sb.append("    storeCard: ").append(toIndentedString(storeCard)).append("\n");
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

