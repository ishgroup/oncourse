package ish.oncourse.webservices.usi;

import au.gov.usi._2015.ws.VerifyUSIResponseType;
import au.gov.usi._2015.ws.servicepolicy.IUSIServiceVerifyUSIErrorInfoFaultFaultMessageSingle;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.cxf.bus.spring.BusApplicationContext;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.util.GregorianCalendar;

/**
 * Created by akoira on 11/8/16.
 */
public class UsiService2015Test {

    @Ignore
    public void test() throws au.gov.usi._2015.ws.servicepolicy.IUSIServiceVerifyUSIErrorInfoFaultFaultMessage, ParseException, DatatypeConfigurationException, IUSIServiceVerifyUSIErrorInfoFaultFaultMessageSingle {
        AnnotationConfigApplicationContext parentContext = new AnnotationConfigApplicationContext(TestConfig.class);

        BusApplicationContext applicationContext = new BusApplicationContext(UsiService2015Test.class.getClassLoader().getResource("application-context-2015.xml"), true, parentContext);

        au.gov.usi._2015.ws.servicepolicy.USIService service = new au.gov.usi._2015.ws.servicepolicy.USIService();
        au.gov.usi._2015.ws.servicepolicy.IUSIService endpoint = service.getWS2007FederationHttpBindingIUSIService();


        au.gov.usi._2015.ws.VerifyUSIType type = new au.gov.usi._2015.ws.VerifyUSIType();
        type.setFirstName("");
        type.setFamilyName("");
        type.setOrgCode("");

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(DateUtils.parseDate("", "yyyy-MM-dd"));
        XMLGregorianCalendar xmlBirthDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        xmlBirthDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

        type.setDateOfBirth(xmlBirthDate);
        type.setUSI("");

        VerifyUSIResponseType responseType = endpoint.verifyUSI(type);
        System.out.println(responseType);
    }
}
