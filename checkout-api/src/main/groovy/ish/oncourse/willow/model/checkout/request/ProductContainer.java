package ish.oncourse.willow.model.checkout.request;


public class ProductContainer  {
  
    private String productId = null;
    private Integer quantity = null;

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

    public ProductContainer productId(String productId) {
      this.productId = productId;
      return this;
    }

    /**
     * Get quantity
     * @return quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
       this.quantity = quantity;
    }

    public ProductContainer quantity(Integer quantity) {
      this.quantity = quantity;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class ProductContainer {\n");
      
      sb.append("    productId: ").append(toIndentedString(productId)).append("\n");
      sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
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

