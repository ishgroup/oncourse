package ish.oncourse.willow.model;

import java.util.ArrayList;
import java.util.List;

public class CourseClassPrice {
    private String fee;
    private String feeOverriden;
    private Discount appliedDiscount;
    private List<Discount> possibleDiscounts = new ArrayList<>();

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getFeeOverriden() {
        return feeOverriden;
    }

    public void setFeeOverriden(String feeOverriden) {
        this.feeOverriden = feeOverriden;
    }

    public Discount getAppliedDiscount() {
        return appliedDiscount;
    }

    public void setAppliedDiscount(Discount appliedDiscount) {
        this.appliedDiscount = appliedDiscount;
    }

    public List<Discount> getPossibleDiscounts() {
        return possibleDiscounts;
    }

    public void setPossibleDiscounts(List<Discount> possibleDiscounts) {
        this.possibleDiscounts = possibleDiscounts;
    }
}
