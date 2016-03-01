package ish.oncourse.admin.pages;


import ish.oncourse.admin.billing.BillingValue;
import ish.oncourse.admin.billing.CollegeValue;
import ish.oncourse.admin.billing.WebSiteValue;
import ish.oncourse.admin.billing.get.BillingContext;
import ish.oncourse.admin.billing.get.GetCollegeValue;
import ish.oncourse.model.College;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Meta;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.RequestParameter;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Meta("tapestry.response-content-type=text/xml")
public class BillingXML {

    @Inject
    private ICollegeService collegeService;

    @Inject
    private ICayenneService cayenneService;


    @Property
    private Date from;

    @Property
    private Date to;

    @Property
    private List<CollegeValue> values;

    @Property
    private CollegeValue value;

    @Property
    private BillingValue collegeBillingValue;

    @Property
    private WebSiteValue webSiteValue;

    @Property
    private BillingValue siteBillingValue;

    @Property
    private Date invoiceDate = new Date();

    @Property
    private Date now = new Date();

    /**
     * /willowAdmin/billingxml?month=12 where 12 is December
     */
    public void onActivate(@RequestParameter(value = "month", allowBlank = true) String month) {
        if (StringUtils.isBlank(month) || !StringUtils.isNumeric(month)) {
            from = DateUtils.truncate(DateUtils.addMonths(now, -1), Calendar.MONTH);
        } else {
            from = DateUtils.truncate(DateUtils.setMonths(now, Integer.valueOf(month) - 1), Calendar.MONTH);
            from = now.after(from) ? from : DateUtils.addYears(from, -1);
        }
    }

    @SetupRender
    public void beforeRender() {
        to = DateUtils.addMonths(from, 1);
        invoiceDate = DateUtils.addDays(to, -1);

        List<College> colleges = collegeService.allColleges();

        ObjectContext context = cayenneService.newContext();
        values = new ArrayList<>();

        for (College college : colleges) {
            values.add(GetCollegeValue.valueOf(BillingContext.valueOf(context, context.localObject(college), from, to)).get());
        }

    }
}
