package ish.oncourse.willow.model.checkout;

import ish.oncourse.willow.model.field.FieldHeading;
import java.util.ArrayList;
import java.util.List;

public class Membership  {
  
    private String contactId = null;
    private String productId = null;
    private List<String> warnings = new ArrayList<String>();
    private List<String> errors = new ArrayList<String>();
    private Double price = null;
    private Boolean selected = null;
    private Boolean allowRemove = null;
    private String relatedClassId = null;
    private String relatedProductId = null;
    private List<FieldHeading> fieldHeadings = new ArrayList<FieldHeading>();

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

    /**
     * Get allowRemove
     * @return allowRemove
     */
    public Boolean getAllowRemove() {
        return allowRemove;
    }

    public void setAllowRemove(Boolean allowRemove) {
       this.allowRemove = allowRemove;
    }

    public Membership allowRemove(Boolean allowRemove) {
      this.allowRemove = allowRemove;
      return this;
    }

    /**
     * Get relatedClassId
     * @return relatedClassId
     */
    public String getRelatedClassId() {
        return relatedClassId;
    }

    public void setRelatedClassId(String relatedClassId) {
       this.relatedClassId = relatedClassId;
    }

    public Membership relatedClassId(String relatedClassId) {
      this.relatedClassId = relatedClassId;
      return this;
    }

    /**
     * Get relatedProductId
     * @return relatedProductId
     */
    public String getRelatedProductId() {
        return relatedProductId;
    }

    public void setRelatedProductId(String relatedProductId) {
       this.relatedProductId = relatedProductId;
    }

    public Membership relatedProductId(String relatedProductId) {
      this.relatedProductId = relatedProductId;
      return this;
    }

    /**
     * Get fieldHeadings
     * @return fieldHeadings
     */
    public List<FieldHeading> getFieldHeadings() {
        return fieldHeadings;
    }

    public void setFieldHeadings(List<FieldHeading> fieldHeadings) {
       this.fieldHeadings = fieldHeadings;
    }

    public Membership fieldHeadings(List<FieldHeading> fieldHeadings) {
      this.fieldHeadings = fieldHeadings;
      return this;
    }

    public Membership addFieldHeadingsItem(FieldHeading fieldHeadingsItem) {
      this.fieldHeadings.add(fieldHeadingsItem);
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
      sb.append("    allowRemove: ").append(toIndentedString(allowRemove)).append("\n");
      sb.append("    relatedClassId: ").append(toIndentedString(relatedClassId)).append("\n");
      sb.append("    relatedProductId: ").append(toIndentedString(relatedProductId)).append("\n");
      sb.append("    fieldHeadings: ").append(toIndentedString(fieldHeadings)).append("\n");
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

