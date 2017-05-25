package ish.oncourse.willow.model.checkout;

import ish.oncourse.willow.model.checkout.Amount;
import ish.oncourse.willow.model.checkout.PurchaseItems;
import ish.oncourse.willow.model.common.CommonError;
import java.util.ArrayList;
import java.util.List;

public class CheckoutModel  {
  
    private CommonError error = null;
    private List<PurchaseItems> purchaseItemsList = new ArrayList<PurchaseItems>();
    private Amount amount = null;
    private List<String> promotionIds = new ArrayList<String>();
    private String payerId = null;

    /**
     * Get error
     * @return error
     */
    public CommonError getError() {
        return error;
    }

    public void setError(CommonError error) {
       this.error = error;
    }

    public CheckoutModel error(CommonError error) {
      this.error = error;
      return this;
    }

    /**
     * Get purchaseItemsList
     * @return purchaseItemsList
     */
    public List<PurchaseItems> getPurchaseItemsList() {
        return purchaseItemsList;
    }

    public void setPurchaseItemsList(List<PurchaseItems> purchaseItemsList) {
       this.purchaseItemsList = purchaseItemsList;
    }

    public CheckoutModel purchaseItemsList(List<PurchaseItems> purchaseItemsList) {
      this.purchaseItemsList = purchaseItemsList;
      return this;
    }

    public CheckoutModel addPurchaseItemsListItem(PurchaseItems purchaseItemsListItem) {
      this.purchaseItemsList.add(purchaseItemsListItem);
      return this;
    }

    /**
     * Get amount
     * @return amount
     */
    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
       this.amount = amount;
    }

    public CheckoutModel amount(Amount amount) {
      this.amount = amount;
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

    public CheckoutModel promotionIds(List<String> promotionIds) {
      this.promotionIds = promotionIds;
      return this;
    }

    public CheckoutModel addPromotionIdsItem(String promotionIdsItem) {
      this.promotionIds.add(promotionIdsItem);
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

    public CheckoutModel payerId(String payerId) {
      this.payerId = payerId;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class CheckoutModel {\n");
      
      sb.append("    error: ").append(toIndentedString(error)).append("\n");
      sb.append("    purchaseItemsList: ").append(toIndentedString(purchaseItemsList)).append("\n");
      sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
      sb.append("    promotionIds: ").append(toIndentedString(promotionIds)).append("\n");
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

