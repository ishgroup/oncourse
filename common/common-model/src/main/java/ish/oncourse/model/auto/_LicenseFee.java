package ish.oncourse.model.auto;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.oncourse.model.College;
import ish.oncourse.model.WebSite;

/**
 * Class _LicenseFee was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _LicenseFee extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String BILLING_MONTH_PROPERTY = "billingMonth";
    @Deprecated
    public static final String FEE_PROPERTY = "fee";
    @Deprecated
    public static final String FREE_TRANSACTIONS_PROPERTY = "freeTransactions";
    @Deprecated
    public static final String KEY_CODE_PROPERTY = "keyCode";
    @Deprecated
    public static final String PAID_UNTIL_PROPERTY = "paidUntil";
    @Deprecated
    public static final String PLAN_NAME_PROPERTY = "planName";
    @Deprecated
    public static final String RENEWAL_DATE_PROPERTY = "renewalDate";
    @Deprecated
    public static final String VALID_UNTIL_PROPERTY = "validUntil";
    @Deprecated
    public static final String COLLEGE_PROPERTY = "college";
    @Deprecated
    public static final String WEB_SITE_PROPERTY = "webSite";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Integer> BILLING_MONTH = new Property<Integer>("billingMonth");
    public static final Property<BigDecimal> FEE = new Property<BigDecimal>("fee");
    public static final Property<Integer> FREE_TRANSACTIONS = new Property<Integer>("freeTransactions");
    public static final Property<String> KEY_CODE = new Property<String>("keyCode");
    public static final Property<Date> PAID_UNTIL = new Property<Date>("paidUntil");
    public static final Property<String> PLAN_NAME = new Property<String>("planName");
    public static final Property<Date> RENEWAL_DATE = new Property<Date>("renewalDate");
    public static final Property<Date> VALID_UNTIL = new Property<Date>("validUntil");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<WebSite> WEB_SITE = new Property<WebSite>("webSite");

    public void setBillingMonth(Integer billingMonth) {
        writeProperty("billingMonth", billingMonth);
    }
    public Integer getBillingMonth() {
        return (Integer)readProperty("billingMonth");
    }

    public void setFee(BigDecimal fee) {
        writeProperty("fee", fee);
    }
    public BigDecimal getFee() {
        return (BigDecimal)readProperty("fee");
    }

    public void setFreeTransactions(Integer freeTransactions) {
        writeProperty("freeTransactions", freeTransactions);
    }
    public Integer getFreeTransactions() {
        return (Integer)readProperty("freeTransactions");
    }

    public void setKeyCode(String keyCode) {
        writeProperty("keyCode", keyCode);
    }
    public String getKeyCode() {
        return (String)readProperty("keyCode");
    }

    public void setPaidUntil(Date paidUntil) {
        writeProperty("paidUntil", paidUntil);
    }
    public Date getPaidUntil() {
        return (Date)readProperty("paidUntil");
    }

    public void setPlanName(String planName) {
        writeProperty("planName", planName);
    }
    public String getPlanName() {
        return (String)readProperty("planName");
    }

    public void setRenewalDate(Date renewalDate) {
        writeProperty("renewalDate", renewalDate);
    }
    public Date getRenewalDate() {
        return (Date)readProperty("renewalDate");
    }

    public void setValidUntil(Date validUntil) {
        writeProperty("validUntil", validUntil);
    }
    public Date getValidUntil() {
        return (Date)readProperty("validUntil");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setWebSite(WebSite webSite) {
        setToOneTarget("webSite", webSite, true);
    }

    public WebSite getWebSite() {
        return (WebSite)readProperty("webSite");
    }


}
