package ish.oncourse.willow.model.web;

import ish.oncourse.willow.model.web.Discount;
import java.util.ArrayList;
import java.util.List;

public class CourseClassPrice  {
  
    private String fee = null;
    private String feeOverriden = null;
    private Discount appliedDiscount = null;
    private List<Discount> possibleDiscounts = new ArrayList<Discount>();
    private Boolean hasTax = null;

    /**
     * Full class price
     * @return fee
     */
    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
       this.fee = fee;
    }

    public CourseClassPrice fee(String fee) {
      this.fee = fee;
      return this;
    }

    /**
     * Class price overriden by application
     * @return feeOverriden
     */
    public String getFeeOverriden() {
        return feeOverriden;
    }

    public void setFeeOverriden(String feeOverriden) {
       this.feeOverriden = feeOverriden;
    }

    public CourseClassPrice feeOverriden(String feeOverriden) {
      this.feeOverriden = feeOverriden;
      return this;
    }

    /**
     * Get appliedDiscount
     * @return appliedDiscount
     */
    public Discount getAppliedDiscount() {
        return appliedDiscount;
    }

    public void setAppliedDiscount(Discount appliedDiscount) {
       this.appliedDiscount = appliedDiscount;
    }

    public CourseClassPrice appliedDiscount(Discount appliedDiscount) {
      this.appliedDiscount = appliedDiscount;
      return this;
    }

    /**
     * List discounts sorted by discounted fee
     * @return possibleDiscounts
     */
    public List<Discount> getPossibleDiscounts() {
        return possibleDiscounts;
    }

    public void setPossibleDiscounts(List<Discount> possibleDiscounts) {
       this.possibleDiscounts = possibleDiscounts;
    }

    public CourseClassPrice possibleDiscounts(List<Discount> possibleDiscounts) {
      this.possibleDiscounts = possibleDiscounts;
      return this;
    }

    public CourseClassPrice addPossibleDiscountsItem(Discount possibleDiscountsItem) {
      this.possibleDiscounts.add(possibleDiscountsItem);
      return this;
    }

    /**
     * Has tax
     * @return hasTax
     */
    public Boolean getHasTax() {
        return hasTax;
    }

    public void setHasTax(Boolean hasTax) {
       this.hasTax = hasTax;
    }

    public CourseClassPrice hasTax(Boolean hasTax) {
      this.hasTax = hasTax;
      return this;
    }


    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class CourseClassPrice {\n");
      
      sb.append("    fee: ").append(toIndentedString(fee)).append("\n");
      sb.append("    feeOverriden: ").append(toIndentedString(feeOverriden)).append("\n");
      sb.append("    appliedDiscount: ").append(toIndentedString(appliedDiscount)).append("\n");
      sb.append("    possibleDiscounts: ").append(toIndentedString(possibleDiscounts)).append("\n");
      sb.append("    hasTax: ").append(toIndentedString(hasTax)).append("\n");
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

