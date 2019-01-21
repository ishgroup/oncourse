package ish.common.types;

import java.util.Date;

public class LocateUSIRequest {

    private String orgCode;
    private String firstName;
    private String middleName;
    private String familyName;
    private USIGender gender;
    private Date dateOfBirth;
    private String townCityOfBirth;
    private String emailAddress;

    public String getOrgCode() { return orgCode; }

    public void setOrgCode(String orgCode) { this.orgCode = orgCode; }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public USIGender getGender() {
        return gender;
    }

    public void setGender(USIGender gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getTownCityOfBirth() {
        return townCityOfBirth;
    }

    public void setTownCityOfBirth(String townCityOfBirth) {
        this.townCityOfBirth = townCityOfBirth;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
