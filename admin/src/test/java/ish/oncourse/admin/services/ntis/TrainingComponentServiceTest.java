package ish.oncourse.admin.services.ntis;

import au.gov.training.services.trainingcomponent.ITrainingComponentService;
import ish.oncourse.admin.services.AdminTestModule;
import org.apache.tapestry5.test.PageTester;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;

public class TrainingComponentServiceTest   {


	/**
     * This method performs ten calls to the GetServerTime operation.
     */
    @Test
    public void testGetServerTime() throws Exception {
        PageTester tester =  new PageTester("ish.oncourse.admin.services", "appName", "src/main/webapp",  AdminTestModule.class);

    	ITrainingComponentService port = tester.getRegistry().getService(ITrainingComponentService.class);

        long startTime = System.currentTimeMillis();
        XMLGregorianCalendar serverDate = port.getServerTime();

        long finishTime = System.currentTimeMillis();
        double elapsedTime = (finishTime - startTime) / 1000d; // elapsed time in seconds

        System.out.println("Date retrieved - " + serverDate.toString() + " in " + elapsedTime + " seconds");
        Assert.assertNotNull(serverDate);
    }
}
