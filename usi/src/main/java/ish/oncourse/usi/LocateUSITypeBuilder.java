package ish.oncourse.usi;

import au.gov.usi._2018.ws.ContactDetailsLocateType;
import au.gov.usi._2018.ws.LocateUSIType;
import au.gov.usi._2018.ws.PersonalDetailsLocateType;
import ish.common.types.LocateUSIRequest;
import org.apache.commons.lang3.StringUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Creates valid LocateUSIType request, in case of incomplete set of params return null
 */
public class LocateUSITypeBuilder {

    private LocateUSIRequest internalRq;

    public static LocateUSITypeBuilder valueOf(LocateUSIRequest internalRq) {
        LocateUSITypeBuilder obj = new LocateUSITypeBuilder();
        obj.internalRq = internalRq;
        return obj;
    }

    public LocateUSIType build() {
        LocateUSIType serviceRq = null;
        if (internalRq.getOrgCode() != null && internalRq.getUserReference() != null) {
            PersonalDetailsLocateType personal = createPersonalDetails(internalRq);
            if (personal != null) {
                ContactDetailsLocateType contact = createContactDetails(internalRq);

                serviceRq = new LocateUSIType();
                serviceRq.setOrgCode(internalRq.getOrgCode());
                serviceRq.setUserReference(internalRq.getUserReference());
                serviceRq.setPersonalDetails(personal);
                serviceRq.setContactDetails(contact);
            }
        }
        return serviceRq;
    }

    private PersonalDetailsLocateType createPersonalDetails(LocateUSIRequest rq) {
        PersonalDetailsLocateType obj = null;
        if (rq.getDateOfBirth() != null && rq.getGender() != null) {
            obj = new PersonalDetailsLocateType();
            obj.setFirstName(strToEmpty(rq.getFirstName()));
            obj.setMiddleName(strToEmpty(rq.getMiddleName()));
            obj.setFamilyName(strToEmpty(rq.getFamilyName()));
            obj.setGender(rq.getGender() != null ? rq.getGender().getRequestCode() : StringUtils.EMPTY);
            obj.setDateOfBirth(dateToXML(rq.getDateOfBirth()));
            obj.setTownCityOfBirth(strToEmpty(rq.getTownCityOfBirth()));
        }
        return obj;
    }

    private ContactDetailsLocateType createContactDetails(LocateUSIRequest rq) {
        ContactDetailsLocateType obj = new ContactDetailsLocateType();
        obj.setEmailAddress(strToEmpty(rq.getEmailAddress()));
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

    // used to avoid usi.gov.au service validation errors
    private String strToEmpty(String val) {
        return val == null ? StringUtils.EMPTY : val;
    }
}
