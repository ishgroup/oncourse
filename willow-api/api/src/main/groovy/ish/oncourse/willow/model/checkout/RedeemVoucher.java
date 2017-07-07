package ish.oncourse.willow.model.checkout;

import ish.oncourse.willow.model.web.Contact;

public class RedeemVoucher  {
  
    private String id = null;
    private Contact payer = null;
    private String name = null;

    /**
     * Get id
     * @return id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
       this.id = id;
    }

    public RedeemVoucher id(String id) {
      this.id = id;
      return this;
    }

    /**
     * Get payer
     * @return payer
     */
    public Contact getPayer() {
        return payer;
    }

    public void setPayer(Contact payer) {
       this.payer = payer;
    }

    public RedeemVoucher payer(Contact payer) {
      this.payer = payer;
      return this;
    }

    /**
     * Get name
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
       this.name = name;
    }

    public RedeemVoucher name(String name) {
      this.name = name;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class RedeemVoucher {\n");
      
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
      sb.append("    payer: ").append(toIndentedString(payer)).append("\n");
      sb.append("    name: ").append(toIndentedString(name)).append("\n");
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

