package ish.oncourse.webservices.usi;

import au.gov.usi._2018.ws.VerifyUSIResponseType;
import au.gov.usi._2018.ws.servicepolicy.IUSIServiceVerifyUSIErrorInfoFaultFaultMessageSingle;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.cxf.bus.spring.BusApplicationContext;
import org.apache.cxf.ws.security.trust.STSClient;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;
import java.text.ParseException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UsiService2018Test {

    @Test
    @Ignore
    public void test() throws au.gov.usi._2018.ws.servicepolicy.IUSIServiceVerifyUSIErrorInfoFaultFaultMessage, ParseException, DatatypeConfigurationException, IUSIServiceVerifyUSIErrorInfoFaultFaultMessageSingle {


        AnnotationConfigApplicationContext parentContext = new AnnotationConfigApplicationContext(TestConfig.class);

        BusApplicationContext applicationContext = new BusApplicationContext(UsiService2018Test.class.getClassLoader().getResource("application-context-2018.xml"), true, parentContext);

        au.gov.usi._2018.ws.servicepolicy.USIService service = new au.gov.usi._2018.ws.servicepolicy.USIService();
        au.gov.usi._2018.ws.servicepolicy.IUSIService endpoint = service.getWS2007FederationHttpBindingIUSIService();


        String uuid  = UUID.randomUUID().toString();
        String usiSoftwareId = "5176926411";
        String ishABN = "74073212736";
        String collegeABN = "53136500313";

        String actAs = String.format(
            "<v13:RelationshipToken ID=\"%s\" xmlns:v13=\"http://vanguard.business.gov.au/2016/03\"><v13:Relationship v13:Type=\"OSPfor\"><v13:Attribute v13:Name=\"SSID\" v13:Value=\"%s\"/></v13:Relationship> <v13:FirstParty v13:Scheme=\"uri://abr.gov.au/ABN\" v13:Value=\"%s\"/><v13:SecondParty v13:Scheme=\"uri://abr.gov.au/ABN\" v13:Value=\"%s\"/></v13:RelationshipToken>", uuid, usiSoftwareId, ishABN, collegeABN);
        Map<String, Object> params = ((BindingProvider)endpoint).getRequestContext();
        ((STSClient)params.get("ws-security.sts.client")).setActAs(actAs);

        au.gov.usi._2018.ws.VerifyUSIType type = new au.gov.usi._2018.ws.VerifyUSIType();
        type.setFirstName("Simone");
        type.setFamilyName("Claudia Stockl");

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(DateUtils.parseDate("1988-05-02", "yyyy-MM-dd"));
        XMLGregorianCalendar xmlBirthDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        xmlBirthDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

        type.setDateOfBirth(xmlBirthDate);
        type.setUSI("DB5PW8SXU6");

        VerifyUSIResponseType responseType = endpoint.verifyUSI(type);

        System.out.println(responseType);
    }








}
