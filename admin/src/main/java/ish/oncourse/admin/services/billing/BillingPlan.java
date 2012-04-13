package ish.oncourse.admin.services.billing;

public enum BillingPlan {
    light("OC-LIGHT"),
    professional("OC-11"),
    enterprise("OC-10"),
    starter("OCW-20"),
    standard("OCW-21"),
    premium("OCW-22"),
    platinum("OCW-23");

    private String productionCode;

    private BillingPlan(String productionCode) {
        this.productionCode = productionCode;
    }

    public String getProductionCode() {
        return productionCode;
    }
}
