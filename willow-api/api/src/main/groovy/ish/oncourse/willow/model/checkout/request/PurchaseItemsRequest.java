package ish.oncourse.willow.model.checkout.request;

import java.util.ArrayList;
import java.util.List;

public class PurchaseItemsRequest  {
  
    private String contactId = null;
    private List<String> classesIds = new ArrayList<String>();
    private List<String> productIds = new ArrayList<String>();
    private List<String> promotionIds = new ArrayList<String>();

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

    public PurchaseItemsRequest contactId(String contactId) {
      this.contactId = contactId;
      return this;
    }

    /**
     * Get classesIds
     * @return classesIds
     */
    public List<String> getClassesIds() {
        return classesIds;
    }

    public void setClassesIds(List<String> classesIds) {
       this.classesIds = classesIds;
    }

    public PurchaseItemsRequest classesIds(List<String> classesIds) {
      this.classesIds = classesIds;
      return this;
    }

    public PurchaseItemsRequest addClassesIdsItem(String classesIdsItem) {
      this.classesIds.add(classesIdsItem);
      return this;
    }

    /**
     * Get productIds
     * @return productIds
     */
    public List<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<String> productIds) {
       this.productIds = productIds;
    }

    public PurchaseItemsRequest productIds(List<String> productIds) {
      this.productIds = productIds;
      return this;
    }

    public PurchaseItemsRequest addProductIdsItem(String productIdsItem) {
      this.productIds.add(productIdsItem);
      return this;
    }

    /**
     * Get promotionIds
     * @return promotionIds
     */
    public List<String> getPromotionIds() {
        return promotionIds;
    }

    public void setPromotionIds(List<String> promotionIds) {
       this.promotionIds = promotionIds;
    }

    public PurchaseItemsRequest promotionIds(List<String> promotionIds) {
      this.promotionIds = promotionIds;
      return this;
    }

    public PurchaseItemsRequest addPromotionIdsItem(String promotionIdsItem) {
      this.promotionIds.add(promotionIdsItem);
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class PurchaseItemsRequest {\n");
      
      sb.append("    contactId: ").append(toIndentedString(contactId)).append("\n");
      sb.append("    classesIds: ").append(toIndentedString(classesIds)).append("\n");
      sb.append("    productIds: ").append(toIndentedString(productIds)).append("\n");
      sb.append("    promotionIds: ").append(toIndentedString(promotionIds)).append("\n");
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

