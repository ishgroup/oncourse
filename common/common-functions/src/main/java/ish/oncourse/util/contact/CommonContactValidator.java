/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.util.contact;

import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonContactValidator {
    
    public static final Date MIN_DATE_OF_BIRTH;
    public static final String INCORRECT_PROPERTY_LENGTH = "The %s cannot exceed %d characters.";
    public static final String DEFAULT_COUNTRY_NAME = "Australia";
    
    static{
        try {
            MIN_DATE_OF_BIRTH = DateUtils.truncate(new SimpleDateFormat(FormatUtils.DATE_FIELD_PARSE_FORMAT).parse("01/01/1900"), Calendar.YEAR);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }
}
