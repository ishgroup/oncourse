package ish.oncourse.willow.model.checkout;

import ish.oncourse.willow.model.checkout.PurchaseItem;
import java.util.List;

public class ProductItem extends PurchaseItem {
  
    private String productId = null;

    /**
     * Get productId
     * @return productId
     */
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
       this.productId = productId;
    }

    public ProductItem productId(String productId) {
      this.productId = productId;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class ProductItem {\n");
      sb.append("    ").append(toIndentedString(super.toString())).append("\n");
      sb.append("    productId: ").append(toIndentedString(productId)).append("\n");
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

