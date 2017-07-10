package ish.oncourse.willow.model.checkout;

import ish.oncourse.willow.model.checkout.ContactNode;
import java.util.ArrayList;
import java.util.List;

public class CheckoutModelRequest  {
  
    private List<ContactNode> contactNodes = new ArrayList<ContactNode>();
    private List<String> promotionIds = new ArrayList<String>();
    private List<String> redeemedVoucherIds = new ArrayList<String>();
    private String payerId = null;

    /**
     * Get contactNodes
     * @return contactNodes
     */
    public List<ContactNode> getContactNodes() {
        return contactNodes;
    }

    public void setContactNodes(List<ContactNode> contactNodes) {
       this.contactNodes = contactNodes;
    }

    public CheckoutModelRequest contactNodes(List<ContactNode> contactNodes) {
      this.contactNodes = contactNodes;
      return this;
    }

    public CheckoutModelRequest addContactNodesItem(ContactNode contactNodesItem) {
      this.contactNodes.add(contactNodesItem);
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

    public CheckoutModelRequest promotionIds(List<String> promotionIds) {
      this.promotionIds = promotionIds;
      return this;
    }

    public CheckoutModelRequest addPromotionIdsItem(String promotionIdsItem) {
      this.promotionIds.add(promotionIdsItem);
      return this;
    }

    /**
     * Get redeemedVoucherIds
     * @return redeemedVoucherIds
     */
    public List<String> getRedeemedVoucherIds() {
        return redeemedVoucherIds;
    }

    public void setRedeemedVoucherIds(List<String> redeemedVoucherIds) {
       this.redeemedVoucherIds = redeemedVoucherIds;
    }

    public CheckoutModelRequest redeemedVoucherIds(List<String> redeemedVoucherIds) {
      this.redeemedVoucherIds = redeemedVoucherIds;
      return this;
    }

    public CheckoutModelRequest addRedeemedVoucherIdsItem(String redeemedVoucherIdsItem) {
      this.redeemedVoucherIds.add(redeemedVoucherIdsItem);
      return this;
    }

    /**
     * Get payerId
     * @return payerId
     */
    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
       this.payerId = payerId;
    }

    public CheckoutModelRequest payerId(String payerId) {
      this.payerId = payerId;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class CheckoutModelRequest {\n");
      
      sb.append("    contactNodes: ").append(toIndentedString(contactNodes)).append("\n");
      sb.append("    promotionIds: ").append(toIndentedString(promotionIds)).append("\n");
      sb.append("    redeemedVoucherIds: ").append(toIndentedString(redeemedVoucherIds)).append("\n");
      sb.append("    payerId: ").append(toIndentedString(payerId)).append("\n");
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

