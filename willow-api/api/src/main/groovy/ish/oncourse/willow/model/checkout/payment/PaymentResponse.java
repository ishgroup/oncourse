package ish.oncourse.willow.model.checkout.payment;

import ish.oncourse.willow.model.checkout.payment.PaymentStatus;

public class PaymentResponse  {
  
    private String sessionId = null;
    private PaymentStatus status = null;
    private String reference = null;

    /**
     * Get sessionId
     * @return sessionId
     */
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
       this.sessionId = sessionId;
    }

    public PaymentResponse sessionId(String sessionId) {
      this.sessionId = sessionId;
      return this;
    }

    /**
     * Get status
     * @return status
     */
    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
       this.status = status;
    }

    public PaymentResponse status(PaymentStatus status) {
      this.status = status;
      return this;
    }

    /**
     * Get reference
     * @return reference
     */
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
       this.reference = reference;
    }

    public PaymentResponse reference(String reference) {
      this.reference = reference;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class PaymentResponse {\n");
      
      sb.append("    sessionId: ").append(toIndentedString(sessionId)).append("\n");
      sb.append("    status: ").append(toIndentedString(status)).append("\n");
      sb.append("    reference: ").append(toIndentedString(reference)).append("\n");
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

