/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.pages;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Meta;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

@Meta("tapestry.response-content-type=text/plain")
public class RobotsTXT {

    @Inject
    private Request request;

    @Inject
    private Messages messages;
    
    private static final String ROBOTS_CONTENT = "robots-txt-content";
    private static final String ROBOTS_CONTENT_TYPE = "text/plain";


    StreamResponse onActivate() {
        return new TextStreamResponse(ROBOTS_CONTENT_TYPE, messages.format(ROBOTS_CONTENT, request.getServerName()));
    }
    
}
