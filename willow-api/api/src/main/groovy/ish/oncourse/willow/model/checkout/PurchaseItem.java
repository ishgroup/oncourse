package ish.oncourse.willow.model.checkout;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.ArrayList;
import java.util.List;

public class PurchaseItem  {
  
    private Boolean enabled = null;
    private String contactId = null;
    private String title = null;
    private List<String> warnings = new ArrayList<String>();
    private List<String> errors = new ArrayList<String>();
    private String price = null;
    private String discount = null;

    /**
     * Get enabled
     * @return enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
       this.enabled = enabled;
    }

    public PurchaseItem enabled(Boolean enabled) {
      this.enabled = enabled;
      return this;
    }

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

    public PurchaseItem contactId(String contactId) {
      this.contactId = contactId;
      return this;
    }

    /**
     * Get title
     * @return title
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
       this.title = title;
    }

    public PurchaseItem title(String title) {
      this.title = title;
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

    public PurchaseItem warnings(List<String> warnings) {
      this.warnings = warnings;
      return this;
    }

    public PurchaseItem addWarningsItem(String warningsItem) {
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

    public PurchaseItem errors(List<String> errors) {
      this.errors = errors;
      return this;
    }

    public PurchaseItem addErrorsItem(String errorsItem) {
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

    public PurchaseItem price(String price) {
      this.price = price;
      return this;
    }

    /**
     * Get discount
     * @return discount
     */
    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
       this.discount = discount;
    }

    public PurchaseItem discount(String discount) {
      this.discount = discount;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class PurchaseItem {\n");
      
      sb.append("    enabled: ").append(toIndentedString(enabled)).append("\n");
      sb.append("    contactId: ").append(toIndentedString(contactId)).append("\n");
      sb.append("    title: ").append(toIndentedString(title)).append("\n");
      sb.append("    warnings: ").append(toIndentedString(warnings)).append("\n");
      sb.append("    errors: ").append(toIndentedString(errors)).append("\n");
      sb.append("    price: ").append(toIndentedString(price)).append("\n");
      sb.append("    discount: ").append(toIndentedString(discount)).append("\n");
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

