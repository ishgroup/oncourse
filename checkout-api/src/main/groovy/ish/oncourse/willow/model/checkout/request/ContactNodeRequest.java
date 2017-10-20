package ish.oncourse.willow.model.checkout.request;

import java.util.ArrayList;
import java.util.List;

public class ContactNodeRequest  {
  
    private String contactId = null;
    private List<String> classIds = new ArrayList<String>();
    private List<String> waitingCourseIds = new ArrayList<String>();
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

    public ContactNodeRequest contactId(String contactId) {
      this.contactId = contactId;
      return this;
    }

    /**
     * Get classIds
     * @return classIds
     */
    public List<String> getClassIds() {
        return classIds;
    }

    public void setClassIds(List<String> classIds) {
       this.classIds = classIds;
    }

    public ContactNodeRequest classIds(List<String> classIds) {
      this.classIds = classIds;
      return this;
    }

    public ContactNodeRequest addClassIdsItem(String classIdsItem) {
      this.classIds.add(classIdsItem);
      return this;
    }

    /**
     * Get waitingCourseIds
     * @return waitingCourseIds
     */
    public List<String> getWaitingCourseIds() {
        return waitingCourseIds;
    }

    public void setWaitingCourseIds(List<String> waitingCourseIds) {
       this.waitingCourseIds = waitingCourseIds;
    }

    public ContactNodeRequest waitingCourseIds(List<String> waitingCourseIds) {
      this.waitingCourseIds = waitingCourseIds;
      return this;
    }

    public ContactNodeRequest addWaitingCourseIdsItem(String waitingCourseIdsItem) {
      this.waitingCourseIds.add(waitingCourseIdsItem);
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

    public ContactNodeRequest productIds(List<String> productIds) {
      this.productIds = productIds;
      return this;
    }

    public ContactNodeRequest addProductIdsItem(String productIdsItem) {
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

    public ContactNodeRequest promotionIds(List<String> promotionIds) {
      this.promotionIds = promotionIds;
      return this;
    }

    public ContactNodeRequest addPromotionIdsItem(String promotionIdsItem) {
      this.promotionIds.add(promotionIdsItem);
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class ContactNodeRequest {\n");
      
      sb.append("    contactId: ").append(toIndentedString(contactId)).append("\n");
      sb.append("    classIds: ").append(toIndentedString(classIds)).append("\n");
      sb.append("    waitingCourseIds: ").append(toIndentedString(waitingCourseIds)).append("\n");
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

