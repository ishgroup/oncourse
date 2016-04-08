package ish.validation;

import ish.oncourse.cayenne.ContactInterface;

public enum PropertyMaxLength {

    firstName(ContactInterface.FIRST_NAME_KEY, 128),
    lastName(ContactInterface.LAST_NAME_KEY, 128),
    postCode(ContactInterface.POSTCODE_KEY, 20),
    state(ContactInterface.STATE_KEY, 20),
    mobilePhone(ContactInterface.MOBILE_PHONE_KEY, 20),
    homePhone(ContactInterface.PHONE_HOME_KEY, 20),
    fax(ContactInterface.FAX_KEY, 20),
    email(ContactInterface.EMAIL_KEY, 100),
    street(ContactInterface.STREET_KEY, 200);

    private String propertyKey;
    private int maxLength;

    PropertyMaxLength(String propertyKey, int maxLength) {
        this.propertyKey = propertyKey;
        this.maxLength = maxLength;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public int getMaxLength() {
        return maxLength;
    }
}
