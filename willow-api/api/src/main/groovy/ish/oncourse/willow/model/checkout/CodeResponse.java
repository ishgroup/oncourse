package ish.oncourse.willow.model.checkout;

import ish.oncourse.willow.model.checkout.RedeemVoucher;
import ish.oncourse.willow.model.web.Promotion;

public class CodeResponse  {
  
    private Promotion promotiom = null;
    private RedeemVoucher voucher = null;

    /**
     * Get promotiom
     * @return promotiom
     */
    public Promotion getPromotiom() {
        return promotiom;
    }

    public void setPromotiom(Promotion promotiom) {
       this.promotiom = promotiom;
    }

    public CodeResponse promotiom(Promotion promotiom) {
      this.promotiom = promotiom;
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
      
      sb.append("    promotiom: ").append(toIndentedString(promotiom)).append("\n");
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

