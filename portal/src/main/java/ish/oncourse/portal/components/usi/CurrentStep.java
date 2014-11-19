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
    private Block step1,
            step1Done,
            step1Failed,
            step2,
            step3,
            step3Done,
            done,
            wait;

    public Block getBlock()
    {
        switch (usiController.getStep())
        {

            case step1:
                return step1;
            case step1Done:
                return step1Done;
            case step1Failed:
                return step1Failed;
            case step2:
                return step2;
            case step3:
                return step3;
            case step3Done:
                return step3Done;
            case done:
                return done;
            case wait:
                return wait;
            default:
                throw new IllegalArgumentException();
        }
    }


}
