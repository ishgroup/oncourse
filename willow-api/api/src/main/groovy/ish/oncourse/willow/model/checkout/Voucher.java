package ish.oncourse.willow.model.checkout;

import java.util.ArrayList;
import java.util.List;

public class Voucher  {
  
    private String contactId = null;
    private String productId = null;
    private List<String> warnings = new ArrayList<String>();
    private List<String> errors = new ArrayList<String>();
    private String price = null;
    private String value = null;
    private List<String> classes = new ArrayList<String>();
    private Boolean selected = null;
    private Boolean isEditablePrice = null;

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

    public Voucher contactId(String contactId) {
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

    public Voucher productId(String productId) {
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

    public Voucher warnings(List<String> warnings) {
      this.warnings = warnings;
      return this;
    }

    public Voucher addWarningsItem(String warningsItem) {
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

    public Voucher errors(List<String> errors) {
      this.errors = errors;
      return this;
    }

    public Voucher addErrorsItem(String errorsItem) {
      this.errors.add(errorsItem);
      return this;
    }

    /**
     * Get price
     * @return price
     */
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
       this.price = price;
    }

    public Voucher price(String price) {
      this.price = price;
      return this;
    }

    /**
     * Get value
     * @return value
     */
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
       this.value = value;
    }

    public Voucher value(String value) {
      this.value = value;
      return this;
    }

    /**
     * Get classes
     * @return classes
     */
    public List<String> getClasses() {
        return classes;
    }

    public void setClasses(List<String> classes) {
       this.classes = classes;
    }

    public Voucher classes(List<String> classes) {
      this.classes = classes;
      return this;
    }

    public Voucher addClassesItem(String classesItem) {
      this.classes.add(classesItem);
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

    public Voucher selected(Boolean selected) {
      this.selected = selected;
      return this;
    }

    /**
     * Get isEditablePrice
     * @return isEditablePrice
     */
    public Boolean getIsEditablePrice() {
        return isEditablePrice;
    }

    public void setIsEditablePrice(Boolean isEditablePrice) {
       this.isEditablePrice = isEditablePrice;
    }

    public Voucher isEditablePrice(Boolean isEditablePrice) {
      this.isEditablePrice = isEditablePrice;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class Voucher {\n");
      
      sb.append("    contactId: ").append(toIndentedString(contactId)).append("\n");
      sb.append("    productId: ").append(toIndentedString(productId)).append("\n");
      sb.append("    warnings: ").append(toIndentedString(warnings)).append("\n");
      sb.append("    errors: ").append(toIndentedString(errors)).append("\n");
      sb.append("    price: ").append(toIndentedString(price)).append("\n");
      sb.append("    value: ").append(toIndentedString(value)).append("\n");
      sb.append("    classes: ").append(toIndentedString(classes)).append("\n");
      sb.append("    selected: ").append(toIndentedString(selected)).append("\n");
      sb.append("    isEditablePrice: ").append(toIndentedString(isEditablePrice)).append("\n");
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

