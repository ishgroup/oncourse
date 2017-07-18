package ish.oncourse.willow.model.checkout.corporatepass;

import ish.oncourse.willow.model.checkout.CheckoutModelRequest;

public class MakeCorporatePassRequest  {
  
    private CheckoutModelRequest checkoutModelRequest = null;
    private Boolean agreementFlag = null;

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

    public MakeCorporatePassRequest checkoutModelRequest(CheckoutModelRequest checkoutModelRequest) {
      this.checkoutModelRequest = checkoutModelRequest;
      return this;
    }

    /**
     * Get agreementFlag
     * @return agreementFlag
     */
    public Boolean getAgreementFlag() {
        return agreementFlag;
    }

    public void setAgreementFlag(Boolean agreementFlag) {
       this.agreementFlag = agreementFlag;
    }

    public MakeCorporatePassRequest agreementFlag(Boolean agreementFlag) {
      this.agreementFlag = agreementFlag;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class MakeCorporatePassRequest {\n");
      
      sb.append("    checkoutModelRequest: ").append(toIndentedString(checkoutModelRequest)).append("\n");
      sb.append("    agreementFlag: ").append(toIndentedString(agreementFlag)).append("\n");
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

