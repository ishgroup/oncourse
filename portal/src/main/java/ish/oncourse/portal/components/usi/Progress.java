package ish.oncourse.portal.components.usi;

import ish.oncourse.portal.usi.UsiController;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class Progress {

    private static final String CLASS_active = "active";
    private static final String CLASS_finished = "finished ";

    @Parameter(required = true)
    @Property
    private UsiController usiController;

    public String getClassUSI()
    {
       switch (usiController.getStep())
       {
           case contactInfo:
           case contactInfoDone:
           case avetmissInfo:
               return StringUtils.EMPTY;
           case usi:
           case usiFailed:
           case waitVerify:
           case waitLocate:
               return CLASS_active;
           default:
               return CLASS_finished;
       }
    }

    public String getClassAvetmissInfo()
    {
        switch (usiController.getStep())
        {
            case contactInfo:
                return StringUtils.EMPTY;
            case avetmissInfo:
                return CLASS_active;
            default:
                return CLASS_finished;
        }
    }

    public String getClassContactInfo()
    {
        switch (usiController.getStep())
        {
            case contactInfo:
                return CLASS_active;
            default:
                return CLASS_finished;
        }
    }


}
