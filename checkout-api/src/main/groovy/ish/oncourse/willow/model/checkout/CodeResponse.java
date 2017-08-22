package ish.oncourse.willow.model.checkout;

import ish.oncourse.willow.model.checkout.RedeemVoucher;
import ish.oncourse.willow.model.web.Promotion;

public class CodeResponse  {
  
    private Promotion promotion = null;
    private RedeemVoucher voucher = null;

    /**
     * Get promotion
     * @return promotion
     */
    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
       this.promotion = promotion;
    }

    public CodeResponse promotion(Promotion promotion) {
      this.promotion = promotion;
      return this;
    }

    /**
     * Get voucher
     * @return voucher
     */
    public RedeemVoucher getVoucher() {
        return voucher;
    }

    public void setVoucher(RedeemVoucher voucher) {
       this.voucher = voucher;
    }

    public CodeResponse voucher(RedeemVoucher voucher) {
      this.voucher = voucher;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class CodeResponse {\n");
      
      sb.append("    promotion: ").append(toIndentedString(promotion)).append("\n");
      sb.append("    voucher: ").append(toIndentedString(voucher)).append("\n");
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

