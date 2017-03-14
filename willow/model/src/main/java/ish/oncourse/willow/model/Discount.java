package ish.oncourse.willow.model;

import java.util.Date;

public class Discount {
    private long id;
    private Date expiryDate;
    private double discountedFee;
    private double discountValue;
    private String title;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public double getDiscountedFee() {
        return discountedFee;
    }

    public void setDiscountedFee(double discountedFee) {
        this.discountedFee = discountedFee;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
