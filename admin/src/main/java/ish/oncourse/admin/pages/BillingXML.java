package ish.oncourse.admin.pages;


import ish.oncourse.admin.services.billing.IBillingDataService;
import ish.oncourse.admin.services.billing.StockCodes;
import ish.oncourse.model.College;
import ish.oncourse.model.CustomFee;
import ish.oncourse.model.LicenseFee;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;
import org.apache.commons.lang.time.DateUtils;
import org.apache.tapestry5.annotations.Meta;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Meta("tapestry.response-content-type=text/xml")
public class BillingXML {

   /* private static final SimpleDateFormat DATE_FORMAT_FOR_CURRENT_DATE = new SimpleDateFormat("dd/mm/yy");
    private static final SimpleDateFormat DATE_FROM_MONTH = new SimpleDateFormat("MMMMM yyyy");*/
    private static final String DATE_MONTH_FORMAT = "MMMMM, yyyy";
    private static final String CURRENT_DATE_FORMAT = "ddmmyy";
    public static final String SMS_DESCRIPTION_TEMPLATE = "SMS usage";
    public static final String SMS_KEY = "sms";

    @Property
    private List<College> colleges;

    @Property
    private CustomFee customFee;

    @Property
    private List<WebSite> webSites;

    @Property
    @Persist
    private Date fromMonth;

    @Property
    private Calendar calendar;

    @Property
    @Persist
    private String month;

    @Property
    @Persist
    private Map<Long, Map<Long, Map<String, Object>>> licenseData;

    @Property
    @Persist
    private Map<Long, Map<Long, Map<String, Object>>> billingData;

    @Inject
    private IBillingDataService billingService;

    @Inject
    private ICollegeService collegeService;

    @Inject
    private ICayenneService cayenneService;


    @SetupRender
    public void beforeRender() {

        colleges = collegeService.allColleges();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        fromMonth = DateUtils.truncate(cal.getTime(),Calendar.MONTH);

        for (College college : colleges) {

            for (LicenseFee licenseFee : college.getLicenseFees()) {
                if (licenseFee.getWebSite() == null) {
                    //make license details for college

                } else {
                    //make license details for web sites
                }
            }
        }


       /* licenseData = billingService.getLicenseFeeData();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_MONTH_FORMAT);
        month =  simpleDateFormat.format(cal);
        try {
            fromMonth = simpleDateFormat.parse(month);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar = Calendar.getInstance();
        calendar.setTime(fromMonth);
        calendar.add(Calendar.MONTH, 1);
        */

   /*
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_MONTH_FORMAT);
        String temp = formatter.format(cal.getTime());
        fromMonth = cal.getTime();
        try {
            fromMonth = formatter.parse(temp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        licenseData = billingService.getLicenseFeeData();
        billingData = billingService.getBillingData(fromMonth, cal.getTime());*/

    }


    public Format getDateFormatForCurrentDate() {
        return  new SimpleDateFormat(CURRENT_DATE_FORMAT);
    }

    public Format getDateMonthFormat() {
        return new SimpleDateFormat(DATE_MONTH_FORMAT);
    }

    public Date getCurrentDate() {
        Calendar.getInstance();
        return new Date();
    }

    public Date getFromMonthDate() {
        return new Date();
    }


   /* public WebSite getCurrentWebsite() {
        List<WebSite> webSites = college.getWebSites();
        if (webSiteIndex < webSites.size()) {
            return college.getWebSites().get(webSiteIndex);
        }
        return null;
    }*/

   /* private Long getCurrentWebSiteId() {
        WebSite currentWebsite = getCurrentWebsite();
        return currentWebsite != null ? currentWebsite.getId() : null;
    }
*/
    public String getSmsCode() {

        return StockCodes.SMS.getProductionCode();
    }

    public String getSmsDescription() {
        return SMS_DESCRIPTION_TEMPLATE;
    }

   /* public BigDecimal getSmsUnitPrice() {
        return (BigDecimal) licenseData.get(college.getId()).get(null).get(SMS_KEY);
    }

    public Long getSmsStockQuantity () {
        Long smsQty = (Long) billingData.get(college.getId()).get(null).get(SMS_KEY);
        smsQty = smsQty == null ? 0 : smsQty;

        return smsQty;
    }*/

}
