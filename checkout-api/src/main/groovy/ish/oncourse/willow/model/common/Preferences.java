package ish.oncourse.willow.model.common;


public class Preferences  {
  
    private Boolean corporatePassEnabled = null;
    private Boolean creditCardEnabled = null;
    private Double minAge = null;
    private String termsLabel = null;
    private String termsUrl = null;

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

    /**
     * Terms and conditions display label
     * @return termsLabel
     */
    public String getTermsLabel() {
        return termsLabel;
    }

    public void setTermsLabel(String termsLabel) {
       this.termsLabel = termsLabel;
    }

    public Preferences termsLabel(String termsLabel) {
      this.termsLabel = termsLabel;
      return this;
    }

    /**
     * Terms and conditions page url
     * @return termsUrl
     */
    public String getTermsUrl() {
        return termsUrl;
    }

    public void setTermsUrl(String termsUrl) {
       this.termsUrl = termsUrl;
    }

    public Preferences termsUrl(String termsUrl) {
      this.termsUrl = termsUrl;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Preferences {\n");
      
      sb.append("    corporatePassEnabled: ").append(toIndentedString(corporatePassEnabled)).append("\n");
      sb.append("    creditCardEnabled: ").append(toIndentedString(creditCardEnabled)).append("\n");
      sb.append("    minAge: ").append(toIndentedString(minAge)).append("\n");
      sb.append("    termsLabel: ").append(toIndentedString(termsLabel)).append("\n");
      sb.append("    termsUrl: ").append(toIndentedString(termsUrl)).append("\n");
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

