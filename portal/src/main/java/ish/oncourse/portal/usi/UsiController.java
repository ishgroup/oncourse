package ish.oncourse.portal.usi;

import ish.oncourse.model.Contact;
import ish.oncourse.util.ValidateHandler;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class UsiController {
    private Contact contact;

    private ValidateHandler validateHandler = new ValidateHandler();

    private ValidationResult validationResult = new ValidationResult();

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public ValidateHandler getValidateHandler() {
        return validateHandler;
    }


    public ValidationResult getValidationResult() {
        return validationResult;
    }
}
