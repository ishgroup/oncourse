package ish.oncourse.willow.model.checkout.payment;

import ish.oncourse.willow.model.checkout.PurchaseItems;

public class PaymentRequest  {
  
    private PurchaseItems checkoutModel = null;
    private String creditCardNumber = null;
    private String creditCardName = null;
    private String creditCardExpiry = null;
    private String creditCardCvv = null;
    private Boolean agreementFlag = null;

    /**
     * Get checkoutModel
     * @return checkoutModel
     */
    public PurchaseItems getCheckoutModel() {
        return checkoutModel;
    }

    public void setCheckoutModel(PurchaseItems checkoutModel) {
       this.checkoutModel = checkoutModel;
    }

    public PaymentRequest checkoutModel(PurchaseItems checkoutModel) {
      this.checkoutModel = checkoutModel;
      return this;
    }

    /**
     * Get creditCardNumber
     * @return creditCardNumber
     */
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
       this.creditCardNumber = creditCardNumber;
    }

    public PaymentRequest creditCardNumber(String creditCardNumber) {
      this.creditCardNumber = creditCardNumber;
      return this;
    }

    /**
     * Get creditCardName
     * @return creditCardName
     */
    public String getCreditCardName() {
        return creditCardName;
    }

    public void setCreditCardName(String creditCardName) {
       this.creditCardName = creditCardName;
    }

    public PaymentRequest creditCardName(String creditCardName) {
      this.creditCardName = creditCardName;
      return this;
    }

    /**
     * Get creditCardExpiry
     * @return creditCardExpiry
     */
    public String getCreditCardExpiry() {
        return creditCardExpiry;
    }

    public void setCreditCardExpiry(String creditCardExpiry) {
       this.creditCardExpiry = creditCardExpiry;
    }

    public PaymentRequest creditCardExpiry(String creditCardExpiry) {
      this.creditCardExpiry = creditCardExpiry;
      return this;
    }

    /**
     * Get creditCardCvv
     * @return creditCardCvv
     */
    public String getCreditCardCvv() {
        return creditCardCvv;
    }

    public void setCreditCardCvv(String creditCardCvv) {
       this.creditCardCvv = creditCardCvv;
    }

    public PaymentRequest creditCardCvv(String creditCardCvv) {
      this.creditCardCvv = creditCardCvv;
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

    public PaymentRequest agreementFlag(Boolean agreementFlag) {
      this.agreementFlag = agreementFlag;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class PaymentRequest {\n");
      
      sb.append("    checkoutModel: ").append(toIndentedString(checkoutModel)).append("\n");
      sb.append("    creditCardNumber: ").append(toIndentedString(creditCardNumber)).append("\n");
      sb.append("    creditCardName: ").append(toIndentedString(creditCardName)).append("\n");
      sb.append("    creditCardExpiry: ").append(toIndentedString(creditCardExpiry)).append("\n");
      sb.append("    creditCardCvv: ").append(toIndentedString(creditCardCvv)).append("\n");
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

