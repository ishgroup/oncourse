package ish.oncourse.portal.components.usi;

import ish.oncourse.portal.usi.UsiController;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Parameter;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Progress {

    private static final String CLASS_active = "active";
    private static final String CLASS_finished = "finished ";

    @Parameter(required = true)
    private UsiController usiController;

    public String getClassStep1()
    {
       switch (usiController.getStep())
       {

           case step1:
           case step1Failed:
           case wait:
               return CLASS_active;
           default:
               return CLASS_finished;
       }
    }

    public String getClassStep2()
    {
        switch (usiController.getStep())
        {

            case step1:
            case step1Failed:
            case step1Done:
            case wait:
                return StringUtils.EMPTY;
            case step2:
                return CLASS_active;
            default:
                return CLASS_finished;
        }
    }

    public String getClassStep3()
    {
        switch (usiController.getStep())
        {

            case step1:
            case step1Failed:
            case step1Done:
            case wait:
            case step2:
                return StringUtils.EMPTY;
            case step3:
                return CLASS_active;
            default:
                return CLASS_finished;
        }
    }


}
