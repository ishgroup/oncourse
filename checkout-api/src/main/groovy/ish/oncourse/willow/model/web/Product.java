package ish.oncourse.willow.model.web;


public class Product  {
  
    private String id = null;
    private String code = null;
    private String name = null;
    private String description = null;
    private Boolean isPaymentGatewayEnabled = null;
    private Boolean canBuy = null;

    /**
     * Internal Unique identifier of product
     * @return id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
       this.id = id;
    }

    public Product id(String id) {
      this.id = id;
      return this;
    }

    /**
     * Code of product
     * @return code
     */
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
       this.code = code;
    }

    public Product code(String code) {
      this.code = code;
      return this;
    }

    /**
     * Name of product
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
       this.name = name;
    }

    public Product name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Description of product
     * @return description
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
       this.description = description;
    }

    public Product description(String description) {
      this.description = description;
      return this;
    }

    /**
     * Is payment gateway enabled
     * @return isPaymentGatewayEnabled
     */
    public Boolean getIsPaymentGatewayEnabled() {
        return isPaymentGatewayEnabled;
    }

    public void setIsPaymentGatewayEnabled(Boolean isPaymentGatewayEnabled) {
       this.isPaymentGatewayEnabled = isPaymentGatewayEnabled;
    }

    public Product isPaymentGatewayEnabled(Boolean isPaymentGatewayEnabled) {
      this.isPaymentGatewayEnabled = isPaymentGatewayEnabled;
      return this;
    }

    /**
     * Is on sale and is Web visible
     * @return canBuy
     */
    public Boolean getCanBuy() {
        return canBuy;
    }

    public void setCanBuy(Boolean canBuy) {
       this.canBuy = canBuy;
    }

    public Product canBuy(Boolean canBuy) {
      this.canBuy = canBuy;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Product {\n");
      
      sb.append("    id: ").append(toIndentedString(id)).append("\n");
      sb.append("    code: ").append(toIndentedString(code)).append("\n");
      sb.append("    name: ").append(toIndentedString(name)).append("\n");
      sb.append("    description: ").append(toIndentedString(description)).append("\n");
      sb.append("    isPaymentGatewayEnabled: ").append(toIndentedString(isPaymentGatewayEnabled)).append("\n");
      sb.append("    canBuy: ").append(toIndentedString(canBuy)).append("\n");
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

