package ish.oncourse.portal.components.usi;

import ish.oncourse.portal.usi.UsiController;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class CurrentStep {
    @Parameter(required = true)
    @Property
    private UsiController usiController;

    @Inject
    private Block usiForm,
            usiFailed,
            avetmissInfo,
            contactInfo,
            contactInfoDone,
            done,
            waitVerify,
            waitLocate;

    public Block getBlock()
    {
        switch (usiController.getStep())
        {

            case usi:
                return usiForm;
            case usiFailed:
                return usiFailed;
            case avetmissInfo:
                return avetmissInfo;
            case contactInfo:
                return contactInfo;
            case contactInfoDone:
                return contactInfoDone;
            case done:
                return done;
            case waitVerify:
                return waitVerify;
            case waitLocate:
                return waitLocate;
            default:
                throw new IllegalArgumentException();
        }
    }


}
