package ish.oncourse.willow.model.checkout;

import java.util.ArrayList;
import java.util.List;

public class Membership  {
  
    private String contactId = null;
    private String productId = null;
    private List<String> warnings = new ArrayList<String>();
    private List<String> errors = new ArrayList<String>();
    private Double price = null;
    private Boolean selected = null;

    /**
     * Get contactId
     * @return contactId
     */
    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
       this.contactId = contactId;
    }

    public Membership contactId(String contactId) {
      this.contactId = contactId;
      return this;
    }

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

    public Membership productId(String productId) {
      this.productId = productId;
      return this;
    }

    /**
     * Get warnings
     * @return warnings
     */
    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
       this.warnings = warnings;
    }

    public Membership warnings(List<String> warnings) {
      this.warnings = warnings;
      return this;
    }

    public Membership addWarningsItem(String warningsItem) {
      this.warnings.add(warningsItem);
      return this;
    }

    /**
     * Get errors
     * @return errors
     */
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
       this.errors = errors;
    }

    public Membership errors(List<String> errors) {
      this.errors = errors;
      return this;
    }

    public Membership addErrorsItem(String errorsItem) {
      this.errors.add(errorsItem);
      return this;
    }

    /**
     * Get price
     * @return price
     */
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
       this.price = price;
    }

    public Membership price(Double price) {
      this.price = price;
      return this;
    }

    /**
     * Get selected
     * @return selected
     */
    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
       this.selected = selected;
    }

    public Membership selected(Boolean selected) {
      this.selected = selected;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Membership {\n");
      
      sb.append("    contactId: ").append(toIndentedString(contactId)).append("\n");
      sb.append("    productId: ").append(toIndentedString(productId)).append("\n");
      sb.append("    warnings: ").append(toIndentedString(warnings)).append("\n");
      sb.append("    errors: ").append(toIndentedString(errors)).append("\n");
      sb.append("    price: ").append(toIndentedString(price)).append("\n");
      sb.append("    selected: ").append(toIndentedString(selected)).append("\n");
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

