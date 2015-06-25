package ish.oncourse.webservices.pages;

import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.webservices.jobs.UpdateAmountOwingJob;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.util.TextStreamResponse;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class UpdateAmountOwing {

    @Inject
    private ICayenneService cayenneService;

    public StreamResponse onActivate() {

        UpdateAmountOwingJob updateAmountOwing = new UpdateAmountOwingJob(cayenneService);
        updateAmountOwing.execute();
        return new TextStreamResponse("text/html", "OK");
    }
}
