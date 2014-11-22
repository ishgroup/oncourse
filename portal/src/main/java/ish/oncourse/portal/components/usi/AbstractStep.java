package ish.oncourse.portal.components.usi;

import ish.oncourse.model.Contact;
import ish.oncourse.portal.usi.UsiController;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.annotations.Parameter;

import java.text.DateFormat;
import java.util.Date;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public abstract class AbstractStep {

    @Parameter(required = true)
    private UsiController usiController;


    public Contact getContact() {
        return usiController.getContact();
    }

    public String getDateOfBirth() {
        Date dateOfBirth = usiController.getContact().getDateOfBirth();
        if (dateOfBirth == null) {
            return null;
        }
        return getDateFormat().format(dateOfBirth);
    }

    public DateFormat getDateFormat() {
        return FormatUtils.getDateFormat(FormatUtils.DATE_FIELD_SHOW_FORMAT, usiController.getContact().getCollege().getTimeZone());
    }

    public UsiController getUsiController() {
        return usiController;
    }
}
