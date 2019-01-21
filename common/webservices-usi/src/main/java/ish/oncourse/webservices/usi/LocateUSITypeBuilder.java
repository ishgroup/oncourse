package ish.oncourse.webservices.usi;

import au.gov.usi._2018.ws.ContactDetailsLocateType;
import au.gov.usi._2018.ws.LocateUSIType;
import au.gov.usi._2018.ws.PersonalDetailsLocateType;
import ish.common.types.LocateUSIRequest;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class LocateUSITypeBuilder {

    private LocateUSIRequest internalRq;

    public static LocateUSITypeBuilder valueOf(LocateUSIRequest internalRq) {
        LocateUSITypeBuilder obj = new LocateUSITypeBuilder();
        obj.internalRq = internalRq;
        return obj;
    }

    public LocateUSIType build() {
        LocateUSIType serviceRq = new LocateUSIType();
        serviceRq.setOrgCode(internalRq.getOrgCode());
        serviceRq.setPersonalDetails(createPersonalDetails(internalRq));
        serviceRq.setContactDetails(createContactDetails(internalRq));
        return serviceRq;
    }

    private PersonalDetailsLocateType createPersonalDetails(LocateUSIRequest rq) {
        PersonalDetailsLocateType obj = new PersonalDetailsLocateType();
        obj.setFirstName(rq.getFirstName());
        obj.setMiddleName(rq.getMiddleName());
        obj.setFamilyName(rq.getFamilyName());
        obj.setGender(rq.getGender().getRequestCode());
        obj.setDateOfBirth(dateToXML(rq.getDateOfBirth()));
        obj.setTownCityOfBirth(rq.getTownCityOfBirth());
        return obj;
    }

    private ContactDetailsLocateType createContactDetails(LocateUSIRequest rq) {
        ContactDetailsLocateType obj = new ContactDetailsLocateType();
        obj.setEmailAddress(rq.getEmailAddress());
        return obj;
    }

    private XMLGregorianCalendar dateToXML(Date date) {
        try {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(date);
            XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
            xmlDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
            return xmlDate;
        } catch (DatatypeConfigurationException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
