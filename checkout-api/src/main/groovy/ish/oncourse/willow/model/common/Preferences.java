package ish.oncourse.willow.model.common;


public class Preferences  {
  
    private Boolean corporatePassEnabled = null;
    private Boolean creditCardEnabled = null;
    private String successLink = null;
    private String refundPolicyUrl = null;
    private String featureEnrolmentDisclosure = null;
    private String googleTagmanagerAccount = null;
    private Double minAge = null;

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
     * is credit card enabled preference
     * @return creditCardEnabled
     */
    public Boolean getCreditCardEnabled() {
        return creditCardEnabled;
    }

    public void setCreditCardEnabled(Boolean creditCardEnabled) {
       this.creditCardEnabled = creditCardEnabled;
    }

    public Preferences creditCardEnabled(Boolean creditCardEnabled) {
      this.creditCardEnabled = creditCardEnabled;
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

    /**
     * Google tag manager account
     * @return googleTagmanagerAccount
     */
    public String getGoogleTagmanagerAccount() {
        return googleTagmanagerAccount;
    }

    public void setGoogleTagmanagerAccount(String googleTagmanagerAccount) {
       this.googleTagmanagerAccount = googleTagmanagerAccount;
    }

    public Preferences googleTagmanagerAccount(String googleTagmanagerAccount) {
      this.googleTagmanagerAccount = googleTagmanagerAccount;
      return this;
    }

    /**
     * Minimal age which not requiring parent
     * @return minAge
     */
    public Double getMinAge() {
        return minAge;
    }

    public void setMinAge(Double minAge) {
       this.minAge = minAge;
    }

    public Preferences minAge(Double minAge) {
      this.minAge = minAge;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Preferences {\n");
      
      sb.append("    corporatePassEnabled: ").append(toIndentedString(corporatePassEnabled)).append("\n");
      sb.append("    creditCardEnabled: ").append(toIndentedString(creditCardEnabled)).append("\n");
      sb.append("    successLink: ").append(toIndentedString(successLink)).append("\n");
      sb.append("    refundPolicyUrl: ").append(toIndentedString(refundPolicyUrl)).append("\n");
      sb.append("    featureEnrolmentDisclosure: ").append(toIndentedString(featureEnrolmentDisclosure)).append("\n");
      sb.append("    googleTagmanagerAccount: ").append(toIndentedString(googleTagmanagerAccount)).append("\n");
      sb.append("    minAge: ").append(toIndentedString(minAge)).append("\n");
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

