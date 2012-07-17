package ish.oncourse.admin.services.billing;

import ish.oncourse.model.College;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static ish.oncourse.admin.services.billing.Constants.*;

/**
 * The class use to format report's records for "Export to Moneyworks"
 */
public enum MWExportFormat {
    // Support record format
    SupportFormat(LICENSE_DATA_KEY_SUPPORT, LICENSE_DATA_KEY_SUPPORT_PLAN, TEMPLATE),
    // Hosting record format
    HostingFormat(LICENSE_DATA_KEY_HOSTING, LICENSE_DATA_KEY_HOSTING_PLAN, TEMPLATE);

    private String valueKey;
    private String planKey;
    private String template;

    MWExportFormat(String valueKey, String planKey, String template)
    {
        this.valueKey = valueKey;
        this.planKey = planKey;
        this.template = template;
    }

    public String getValueKey() {
        return valueKey;
    }

    public String getPlanKey() {
        return planKey;
    }


    public String format(Map<Long, Map<String, Object>> licenseData, College college, Date paidUntil, Date renewalDate, String description)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_MONTH_FORMAT);
        String billingPlan = String.valueOf(licenseData.get(college.getId()).get(getPlanKey()));
        return MessageFormat.format(template,
                college.getBillingCode(),
                BillingPlan.valueOf(billingPlan).getProductionCode(),
                billingPlan,
                getValueKey(),
                college.getName(),
                formatter.format(paidUntil),
                formatter.format(renewalDate),
                licenseData.get(college.getId()).get(getValueKey()),description);
    }

}
