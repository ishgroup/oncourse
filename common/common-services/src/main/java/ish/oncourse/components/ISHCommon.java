package ish.oncourse.components;

import ish.math.Money;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

/**
 * The class contains common methods and common properties which can be used in any tapestry templates.
 * Any Tapestry Component or Page class should extend the class
 */
public abstract class ISHCommon {
    @Inject
    @Property
    private Request request;

    @Property
    private String ISO8601 = "yyyy-MM-dd'T'HH:mm:ssZZ";


    public String formatMoney(Money money, String pattern) {
        NumberFormat format = new DecimalFormat(pattern);
        return format.format(money);
    }

    public String formatDate(Date date, String pattern) {
        DateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }
}
