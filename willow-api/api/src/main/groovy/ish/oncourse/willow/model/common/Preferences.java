package ish.oncourse.willow.model.common;


public class Preferences  {
  
    private Boolean corporatePassEnabled = null;
    private String successLink = null;
    private String refundPolicyUrl = null;
    private String featureEnrolmentDisclosure = null;

    /**
     * is corporate pass enabled preference
     * @return corporatePassEnabled
     */
    public Boolean getCorporatePassEnabled() {
        return corporatePassEnabled;
    }

    public void setCorporatePassEnabled(Boolean corporatePassEnabled) {
       this.corporatePassEnabled = corporatePassEnabled;
    }

    public Preferences corporatePassEnabled(Boolean corporatePassEnabled) {
      this.corporatePassEnabled = corporatePassEnabled;
      return this;
    }

    /**
     * After payment successful redirect user to URL
     * @return successLink
     */
    public String getSuccessLink() {
        return successLink;
    }

    public void setSuccessLink(String successLink) {
       this.successLink = successLink;
    }

    public Preferences successLink(String successLink) {
      this.successLink = successLink;
      return this;
    }

    /**
     * Terms and conditions page
     * @return refundPolicyUrl
     */
    public String getRefundPolicyUrl() {
        return refundPolicyUrl;
    }

    public void setRefundPolicyUrl(String refundPolicyUrl) {
       this.refundPolicyUrl = refundPolicyUrl;
    }

    public Preferences refundPolicyUrl(String refundPolicyUrl) {
      this.refundPolicyUrl = refundPolicyUrl;
      return this;
    }

    /**
     * Student Information
     * @return featureEnrolmentDisclosure
     */
    public String getFeatureEnrolmentDisclosure() {
        return featureEnrolmentDisclosure;
    }

    public void setFeatureEnrolmentDisclosure(String featureEnrolmentDisclosure) {
       this.featureEnrolmentDisclosure = featureEnrolmentDisclosure;
    }

    public Preferences featureEnrolmentDisclosure(String featureEnrolmentDisclosure) {
      this.featureEnrolmentDisclosure = featureEnrolmentDisclosure;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Preferences {\n");
      
      sb.append("    corporatePassEnabled: ").append(toIndentedString(corporatePassEnabled)).append("\n");
      sb.append("    successLink: ").append(toIndentedString(successLink)).append("\n");
      sb.append("    refundPolicyUrl: ").append(toIndentedString(refundPolicyUrl)).append("\n");
      sb.append("    featureEnrolmentDisclosure: ").append(toIndentedString(featureEnrolmentDisclosure)).append("\n");
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

