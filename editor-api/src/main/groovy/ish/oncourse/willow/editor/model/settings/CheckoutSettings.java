package ish.oncourse.willow.editor.model.settings;


public class CheckoutSettings  {
  
    private String successUrl = null;
    private String refundPolicy = null;

    /**
     * Get successUrl
     * @return successUrl
     */
    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
       this.successUrl = successUrl;
    }

    public CheckoutSettings successUrl(String successUrl) {
      this.successUrl = successUrl;
      return this;
    }

    /**
     * Get refundPolicy
     * @return refundPolicy
     */
    public String getRefundPolicy() {
        return refundPolicy;
    }

    public void setRefundPolicy(String refundPolicy) {
       this.refundPolicy = refundPolicy;
    }

    public CheckoutSettings refundPolicy(String refundPolicy) {
      this.refundPolicy = refundPolicy;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class CheckoutSettings {\n");
      
      sb.append("    successUrl: ").append(toIndentedString(successUrl)).append("\n");
      sb.append("    refundPolicy: ").append(toIndentedString(refundPolicy)).append("\n");
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

