package ish.oncourse.admin.services.ntis;

import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;

import junit.framework.Assert;

import org.apache.cxf.ws.security.SecurityConstants;
import org.junit.Test;

import au.gov.training.services.trainingcomponent.ITrainingComponentService;
import au.gov.training.services.trainingcomponent.TrainingComponentService;

public class TrainingComponentServiceTest {
	
	/**
     * This method performs ten calls to the GetServerTime operation.
     */
    @Test
    public void testGetServerTime() throws Exception {

        TrainingComponentService ss = new TrainingComponentService();
        ITrainingComponentService port = ss.getTrainingComponentServiceBasicHttpEndpoint();

        Map<String, Object> ctx = ((BindingProvider) port).getRequestContext();
        
        ctx.put(SecurityConstants.USERNAME, "WebService.Read");
        ctx.put(SecurityConstants.PASSWORD, "Asdf098");
        ctx.put(SecurityConstants.TIMESTAMP_FUTURE_TTL, "30");

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
