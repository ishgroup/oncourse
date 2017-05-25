package ish.oncourse.willow.model.checkout.payment;

import ish.oncourse.willow.model.checkout.payment.PaymentStatus;
import java.util.ArrayList;
import java.util.List;

public class PaymentResponse  {
  
    private String sessionId = null;
    private PaymentStatus paymentStatus = null;
    private String error = null;
    private String paymentReference = null;
    private List<String> applicationIds = new ArrayList<String>();

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
     * Get paymentStatus
     * @return paymentStatus
     */
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
       this.paymentStatus = paymentStatus;
    }

    public PaymentResponse paymentStatus(PaymentStatus paymentStatus) {
      this.paymentStatus = paymentStatus;
      return this;
    }

    /**
     * Get error
     * @return error
     */
    public String getError() {
        return error;
    }

    public void setError(String error) {
       this.error = error;
    }

    public PaymentResponse error(String error) {
      this.error = error;
      return this;
    }

    /**
     * Get paymentReference
     * @return paymentReference
     */
    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
       this.paymentReference = paymentReference;
    }

    public PaymentResponse paymentReference(String paymentReference) {
      this.paymentReference = paymentReference;
      return this;
    }

    /**
     * Get applicationIds
     * @return applicationIds
     */
    public List<String> getApplicationIds() {
        return applicationIds;
    }

    public void setApplicationIds(List<String> applicationIds) {
       this.applicationIds = applicationIds;
    }

    public PaymentResponse applicationIds(List<String> applicationIds) {
      this.applicationIds = applicationIds;
      return this;
    }

    public PaymentResponse addApplicationIdsItem(String applicationIdsItem) {
      this.applicationIds.add(applicationIdsItem);
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class PaymentResponse {\n");
      
      sb.append("    sessionId: ").append(toIndentedString(sessionId)).append("\n");
      sb.append("    paymentStatus: ").append(toIndentedString(paymentStatus)).append("\n");
      sb.append("    error: ").append(toIndentedString(error)).append("\n");
      sb.append("    paymentReference: ").append(toIndentedString(paymentReference)).append("\n");
      sb.append("    applicationIds: ").append(toIndentedString(applicationIds)).append("\n");
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

