package ish.oncourse.admin.billing
/**
 *
 * akoiro - 2/24/16.
 */
enum LicenseCode {
    sms("sms", "SMS usage", null),
    support("support", null, "{0} plan for {1}: 1 month (contract until {2})"),
    ccOffice("cc-office", "Office credit card transaction fee", "Office credit card transaction fee ({0} less {1} free transactions)"),

    hosting("hosting", null, "{0} web plan for {1}: 1 month (contract until {2})"),
    ccWeb("cc-web", null, "{0} online credit card transaction fee"),
    ecommerce("ecommerce", "{0} eCommerce fee at {1}% of {2}", "{0} eCommerce fee at {1}% of {2} ({3} less pre-paid bundle) ")

    def String dbValue
    def String simpleDesc
    def String descTemplate

    LicenseCode(String dbValue, String simpleDesc, String descTemplate) {
        this.dbValue = dbValue
        this.simpleDesc = simpleDesc
        this.descTemplate = descTemplate
    }
}
