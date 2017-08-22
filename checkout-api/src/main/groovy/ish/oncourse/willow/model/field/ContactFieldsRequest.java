package ish.oncourse.willow.model.field;

import ish.oncourse.willow.model.field.FieldSet;
import java.util.ArrayList;
import java.util.List;

public class ContactFieldsRequest  {
  
    private String contactId = null;
    private List<String> classIds = new ArrayList<String>();
    private List<String> productIds = new ArrayList<String>();
    private Boolean mandatoryOnly = null;
    private FieldSet fieldSet = null;

    /**
     * Requested contact id
     * @return contactId
     */
    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
       this.contactId = contactId;
    }

    public ContactFieldsRequest contactId(String contactId) {
      this.contactId = contactId;
      return this;
    }

    /**
     * Requested classe's ids
     * @return classIds
     */
    public List<String> getClassIds() {
        return classIds;
    }

    public void setClassIds(List<String> classIds) {
       this.classIds = classIds;
    }

    public ContactFieldsRequest classIds(List<String> classIds) {
      this.classIds = classIds;
      return this;
    }

    public ContactFieldsRequest addClassIdsItem(String classIdsItem) {
      this.classIds.add(classIdsItem);
      return this;
    }

    /**
     * Requested product's ids
     * @return productIds
     */
    public List<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<String> productIds) {
       this.productIds = productIds;
    }

    public ContactFieldsRequest productIds(List<String> productIds) {
      this.productIds = productIds;
      return this;
    }

    public ContactFieldsRequest addProductIdsItem(String productIdsItem) {
      this.productIds.add(productIdsItem);
      return this;
    }

    /**
     * Flag to show only mandatory fields
     * @return mandatoryOnly
     */
    public Boolean getMandatoryOnly() {
        return mandatoryOnly;
    }

    public void setMandatoryOnly(Boolean mandatoryOnly) {
       this.mandatoryOnly = mandatoryOnly;
    }

    public ContactFieldsRequest mandatoryOnly(Boolean mandatoryOnly) {
      this.mandatoryOnly = mandatoryOnly;
      return this;
    }

    /**
     * Get fieldSet
     * @return fieldSet
     */
    public FieldSet getFieldSet() {
        return fieldSet;
    }

    public void setFieldSet(FieldSet fieldSet) {
       this.fieldSet = fieldSet;
    }

    public ContactFieldsRequest fieldSet(FieldSet fieldSet) {
      this.fieldSet = fieldSet;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class ContactFieldsRequest {\n");
      
      sb.append("    contactId: ").append(toIndentedString(contactId)).append("\n");
      sb.append("    classIds: ").append(toIndentedString(classIds)).append("\n");
      sb.append("    productIds: ").append(toIndentedString(productIds)).append("\n");
      sb.append("    mandatoryOnly: ").append(toIndentedString(mandatoryOnly)).append("\n");
      sb.append("    fieldSet: ").append(toIndentedString(fieldSet)).append("\n");
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

