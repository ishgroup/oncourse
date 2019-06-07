package ish.oncourse.willow.model.field;

import ish.oncourse.willow.model.checkout.request.ProductContainer;
import ish.oncourse.willow.model.field.FieldSet;
import java.util.ArrayList;
import java.util.List;

public class ContactFieldsRequest  {
  
    private String contactId = null;
    private List<String> classIds = new ArrayList<String>();
    private List<ProductContainer> products = new ArrayList<ProductContainer>();
    private List<String> waitingCourseIds = new ArrayList<String>();
    private Boolean mandatoryOnly = null;
    private FieldSet fieldSet = null;
    private Boolean isPayer = null;
    private Boolean isParent = null;

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
     * @return products
     */
    public List<ProductContainer> getProducts() {
        return products;
    }

    public void setProducts(List<ProductContainer> products) {
       this.products = products;
    }

    public ContactFieldsRequest products(List<ProductContainer> products) {
      this.products = products;
      return this;
    }

    public ContactFieldsRequest addProductsItem(ProductContainer productsItem) {
      this.products.add(productsItem);
      return this;
    }

    /**
     * Requested waiting list courses ids
     * @return waitingCourseIds
     */
    public List<String> getWaitingCourseIds() {
        return waitingCourseIds;
    }

    public void setWaitingCourseIds(List<String> waitingCourseIds) {
       this.waitingCourseIds = waitingCourseIds;
    }

    public ContactFieldsRequest waitingCourseIds(List<String> waitingCourseIds) {
      this.waitingCourseIds = waitingCourseIds;
      return this;
    }

    public ContactFieldsRequest addWaitingCourseIdsItem(String waitingCourseIdsItem) {
      this.waitingCourseIds.add(waitingCourseIdsItem);
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

    /**
     * Get isPayer
     * @return isPayer
     */
    public Boolean getIsPayer() {
        return isPayer;
    }

    public void setIsPayer(Boolean isPayer) {
       this.isPayer = isPayer;
    }

    public ContactFieldsRequest isPayer(Boolean isPayer) {
      this.isPayer = isPayer;
      return this;
    }

    /**
     * Get isParent
     * @return isParent
     */
    public Boolean getIsParent() {
        return isParent;
    }

    public void setIsParent(Boolean isParent) {
       this.isParent = isParent;
    }

    public ContactFieldsRequest isParent(Boolean isParent) {
      this.isParent = isParent;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class ContactFieldsRequest {\n");
      
      sb.append("    contactId: ").append(toIndentedString(contactId)).append("\n");
      sb.append("    classIds: ").append(toIndentedString(classIds)).append("\n");
      sb.append("    products: ").append(toIndentedString(products)).append("\n");
      sb.append("    waitingCourseIds: ").append(toIndentedString(waitingCourseIds)).append("\n");
      sb.append("    mandatoryOnly: ").append(toIndentedString(mandatoryOnly)).append("\n");
      sb.append("    fieldSet: ").append(toIndentedString(fieldSet)).append("\n");
      sb.append("    isPayer: ").append(toIndentedString(isPayer)).append("\n");
      sb.append("    isParent: ").append(toIndentedString(isParent)).append("\n");
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

