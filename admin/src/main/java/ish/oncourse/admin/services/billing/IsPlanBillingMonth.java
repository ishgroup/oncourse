package ish.oncourse.admin.services.billing;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * akoiro - 2/29/16.
 */
public class IsPlanBillingMonth {
    private String billingPlan;
    private Date paidUntil;
    private Date from;

    public boolean is() {
        return billingPlan != null &&
                (paidUntil == null ||
                        DateUtils.truncate(paidUntil, Calendar.MONTH).compareTo(DateUtils.truncate(from, Calendar.MONTH)) <= 0);

    }

    public static IsPlanBillingMonth valueOf(String billingPlan, Date paidUntil, Date from) {
        IsPlanBillingMonth result = new IsPlanBillingMonth();
        result.billingPlan = billingPlan;
        result.paidUntil = paidUntil;
        result.from = from;
        return result;
    }

}
