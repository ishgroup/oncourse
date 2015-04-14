package ish.oncourse.admin.services.ntis;

import au.gov.training.services.trainingcomponent.ITrainingComponentService;
import ish.oncourse.admin.services.AdminTestModule;
import ish.oncourse.test.ServiceTest;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;

public class TrainingComponentServiceTest extends ServiceTest {
	
	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.admin.services", "", AdminTestModule.class);
	}
	
	/**
     * This method performs ten calls to the GetServerTime operation.
     */
    @Test
    public void testGetServerTime() throws Exception {
    	        
    	ITrainingComponentService port = getService(ITrainingComponentService.class);
        
        for (int i = 0; i < 10; i++) {
        	
            long startTime = System.currentTimeMillis();

            XMLGregorianCalendar serverDate = port.getServerTime();

            long finishTime = System.currentTimeMillis();
            double elapsedTime = (finishTime - startTime) / 1000d; // elapsed time in seconds

            System.out.println("Date retrieved - " + serverDate.toString() + " in " + elapsedTime + " seconds");
            
            Assert.assertNotNull(serverDate);
        }
    }
}
