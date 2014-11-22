package ish.oncourse.portal.components.usi;

import ish.oncourse.model.Enrolment;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.annotations.Property;

import java.util.Date;
import java.util.List;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Step2 extends AbstractStep {

    @Property
    private Enrolment enrolment;

    public List<Enrolment> getEnrolments()
    {
        return getUsiController().getVETEnrolments();
    }

    public String formatDate(Date date)
    {
        return FormatUtils.getDateFormat("dd MMMMM YYYY", enrolment.getCourseClass().getTimeZone()).format(date);
    }
}
