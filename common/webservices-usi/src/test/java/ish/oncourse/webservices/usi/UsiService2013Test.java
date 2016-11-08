package ish.oncourse.webservices.usi;

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

@Deprecated //should be removed after all colleges will move to 2015
public class UsiService2013Test {

    @Ignore
    public void test() throws au.gov.usi._2013.ws.servicepolicy.IUSIServiceVerifyUSIErrorInfoFaultFaultMessage, ParseException, DatatypeConfigurationException {
        AnnotationConfigApplicationContext parentContext = new AnnotationConfigApplicationContext(TestConfig.class);

        BusApplicationContext applicationContext = new BusApplicationContext(UsiService2013Test.class.getClassLoader().getResource("application-context-2013.xml"), true, parentContext);

        au.gov.usi._2013.ws.servicepolicy.USIService service = new au.gov.usi._2013.ws.servicepolicy.USIService();
        au.gov.usi._2013.ws.servicepolicy.IUSIService endpoint = service.getWS2007FederationHttpBindingIUSIService();


        au.gov.usi._2013.ws.VerifyUSIType type = new au.gov.usi._2013.ws.VerifyUSIType();
        type.setFirstName("");
        type.setFamilyName("");
        type.setOrgCode("");

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(DateUtils.parseDate("", "yyyy-MM-dd"));
        XMLGregorianCalendar xmlBirthDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        xmlBirthDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

        type.setDateOfBirth(xmlBirthDate);
        type.setUSI("");

        endpoint.verifyUSI(type);
    }
}
