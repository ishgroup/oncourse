package ish.oncourse.admin.services.billing;

public class Constants {

    /**
     * Format date for  "Export to Moneyworks" report
     */
    public static final String DATE_MONTH_FORMAT = "MMMM, yyyy";

    /**
     * Template for record of  "Export to Moneyworks" report
     */
    public static final String TEMPLATE = "DI\t" +
    		"{0}\t" +
    		"{1}\t" +
    		"{2}\t" +
    		"{3}\t" +
    		"{4, number,0.000}\t" +
    		"{5}\t" +
    		"{6}\n";

    /**
     * Key for support-value of licenseData
     */
    public static final String LICENSE_DATA_KEY_SUPPORT  = "support";

    /**
     * Key for support-plan-value of licenseData
     */
    public static final String LICENSE_DATA_KEY_SUPPORT_PLAN = "support-plan";

    /**
     * Key for hosting-value of licenseData
     */
    public static final String LICENSE_DATA_KEY_HOSTING = "hosting";

    /**
     * Key for hosting-plan-value of licenseData
     */
    public static final String LICENSE_DATA_KEY_HOSTING_PLAN = "hosting-plan";
}
