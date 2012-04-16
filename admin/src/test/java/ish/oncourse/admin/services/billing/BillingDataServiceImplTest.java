package ish.oncourse.admin.services.ntis;

import ish.oncourse.admin.services.billing.BillingPlan;
import ish.oncourse.admin.services.billing.Constants;
import ish.oncourse.admin.services.billing.MWExportFormat;
import ish.oncourse.model.College;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BillingDataServiceImplTest {

    @Test
    public void testHostingFormat()
    {
        String original = "DI\tCHERSON01\tOCW-21\tonCourse standard hosting plan, 1 year valid to March, 2013\t1\t1234567.890\tonCourse March, 2012\n";
       Map<Long, Map<String, Object>> licenseData = new HashMap<Long, Map<String, Object>>();

       Map<String,Object> cData = new HashMap<String, Object>();
        cData.put(MWExportFormat.HostingFormat.getPlanKey(), BillingPlan.standard.name());
        cData.put(MWExportFormat.HostingFormat.getValueKey(), 1234567.89d);
        licenseData.put(1L, cData);

        College college = new College()
        {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public String getBillingCode() {
                return "CHERSON01";
            }
        };

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2012);
        calendar.set(Calendar.MONTH, 2);

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_MONTH_FORMAT);
        String monthAndYear = dateFormat.format(calendar.getTime());
        String description = "onCourse " + monthAndYear;

        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2013);
        calendar.set(Calendar.MONTH, 2);
        String result = MWExportFormat.HostingFormat.format(licenseData, college, calendar.getTime(), description);
        assertEquals("Check result message", result,original);
    }

    @Test
    public void testSupportFormat()
    {
        String original = "DI\tCHERSON01\tOCW-21\tonCourse standard support plan, 1 year valid to March, 2013\t1\t1234567.890\tonCourse March, 2012\n";
        Map<Long, Map<String, Object>> licenseData = new HashMap<Long, Map<String, Object>>();

        Map<String,Object> cData = new HashMap<String, Object>();
        cData.put(MWExportFormat.SupportFormat.getPlanKey(), BillingPlan.standard.name());
        cData.put(MWExportFormat.SupportFormat.getValueKey(), 1234567.89d);
        licenseData.put(1L, cData);

        College college = new College()
        {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public String getBillingCode() {
                return "CHERSON01";
            }
        };

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2012);
        calendar.set(Calendar.MONTH, 2);

        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_MONTH_FORMAT);
        String monthAndYear = dateFormat.format(calendar.getTime());
        String description = "onCourse " + monthAndYear;

        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2013);
        calendar.set(Calendar.MONTH, 2);
        String result = MWExportFormat.SupportFormat.format(licenseData, college, calendar.getTime(), description);
        assertEquals("Check result message", result,original);
    }


}
