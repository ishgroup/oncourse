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

    private String orgCode;
    private String firstName;
    private String middleName;
    private String familyName;
    private String gender;
    private Date dateOfBirth;
    private String townCityOfBirth;
    private String emailAddress;
    private String userReference;



    public static LocateUSITypeBuilder valueOf(String orgCode,
                                               String firstName,
                                               String middleName,
                                               String familyName,
                                               String gender,
                                               Date dateOfBirth,
                                               String townCityOfBirth,
                                               String emailAddress,
                                               String userReference) {
        LocateUSITypeBuilder builder = new LocateUSITypeBuilder();
        builder.orgCode =orgCode;
        builder.firstName =firstName;
        builder.middleName =middleName;
        builder.familyName =familyName;
        builder.gender =gender;
        builder.dateOfBirth =dateOfBirth;
        builder.townCityOfBirth =townCityOfBirth;
        builder.emailAddress =emailAddress;
        builder.userReference =userReference;
        return builder;
    }

    public LocateUSIType build() {
        LocateUSIType serviceRq = null;
        if (orgCode != null && userReference != null) {
            PersonalDetailsLocateType personal = createPersonalDetails();
            if (personal != null) {
                ContactDetailsLocateType contact = createContactDetails();

                serviceRq = new LocateUSIType();
                serviceRq.setOrgCode(orgCode);
                serviceRq.setUserReference(orgCode);
                serviceRq.setPersonalDetails(personal);
                serviceRq.setContactDetails(contact);
            }
        }
        return serviceRq;
    }

    private PersonalDetailsLocateType createPersonalDetails() {
        PersonalDetailsLocateType obj = null;
        if (dateOfBirth != null) {
            obj = new PersonalDetailsLocateType();
            obj.setFirstName(strToEmpty(firstName));
            obj.setMiddleName(strToEmpty(middleName));
            obj.setFamilyName(strToEmpty(familyName));
            obj.setGender(gender != null ? gender : StringUtils.EMPTY);
            obj.setDateOfBirth(dateToXML(dateOfBirth));
            obj.setTownCityOfBirth(strToEmpty(townCityOfBirth));
        }
        return obj;
    }

    private ContactDetailsLocateType createContactDetails() {
        ContactDetailsLocateType obj = new ContactDetailsLocateType();
        obj.setEmailAddress(strToEmpty(emailAddress));
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
