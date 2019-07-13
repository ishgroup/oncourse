package ish.oncourse.willow.model.web;

import ish.oncourse.willow.model.web.ContactParams;
import ish.oncourse.willow.model.web.PromotionParams;
import java.util.ArrayList;
import java.util.List;

public class ProductsParams  {
  
    private List<String> productsIds = new ArrayList<String>();
    private List<PromotionParams> promotions = new ArrayList<PromotionParams>();
    private ContactParams contact = null;

    /**
     * List of requested products
     * @return productsIds
     */
    public List<String> getProductsIds() {
        return productsIds;
    }

    public void setProductsIds(List<String> productsIds) {
       this.productsIds = productsIds;
    }

    public ProductsParams productsIds(List<String> productsIds) {
      this.productsIds = productsIds;
      return this;
    }

    public ProductsParams addProductsIdsItem(String productsIdsItem) {
      this.productsIds.add(productsIdsItem);
      return this;
    }

    /**
     * List of applied promotions
     * @return promotions
     */
    public List<PromotionParams> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<PromotionParams> promotions) {
       this.promotions = promotions;
    }

    public ProductsParams promotions(List<PromotionParams> promotions) {
      this.promotions = promotions;
      return this;
    }

    public ProductsParams addPromotionsItem(PromotionParams promotionsItem) {
      this.promotions.add(promotionsItem);
      return this;
    }

    /**
     * Get contact
     * @return contact
     */
    public ContactParams getContact() {
        return contact;
    }

    public void setContact(ContactParams contact) {
       this.contact = contact;
    }

    public ProductsParams contact(ContactParams contact) {
      this.contact = contact;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class ProductsParams {\n");
      
      sb.append("    productsIds: ").append(toIndentedString(productsIds)).append("\n");
      sb.append("    promotions: ").append(toIndentedString(promotions)).append("\n");
      sb.append("    contact: ").append(toIndentedString(contact)).append("\n");
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

