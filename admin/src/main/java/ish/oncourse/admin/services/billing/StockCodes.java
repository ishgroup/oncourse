package ish.oncourse.admin.services.billing;

public enum StockCodes {
	
    SMS("ON-SMS"),
    WEB_CC("ON-CC-TRANS"),
    OFFICE_CC("ON-NWEB-CC"),
    ECOMMERCE("ON-ECOM-PERC"),
	
	// hosting/support plans
    light("OC-LIGHT"),
    professional("OC-11"),
    enterprise("OC-10"),
    starter("OCW-20"),
    standard("OCW-21"),
    premium("OCW-22"),
    platinum("OCW-23");

    private String productionCode;

    private StockCodes(String productionCode) {
        this.productionCode = productionCode;
    }

    public String getProductionCode() {
        return productionCode;
    }
}
