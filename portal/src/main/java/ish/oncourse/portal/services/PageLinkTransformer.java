/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.portal.services;

import ish.oncourse.services.persistence.ICayenneService;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.internal.AbstractEventContext;
import org.apache.tapestry5.internal.EmptyEventContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderRequestParameters;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.linktransform.PageRenderLinkTransformer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageLinkTransformer implements PageRenderLinkTransformer {
    //https://skillsoncourse.com.au/portal/usi/uniqueCode?valid=21140101&key=k9_S8uk68W5PoCvq5lSUp70sqQY
    public static final Pattern REGEXP_USI_PATH = Pattern.compile("^/usi/(.*)$");
    public static final Pattern REGEXP_USI_CONTEXT = Pattern.compile("^([^?.]*)?(.*)$");


    @Inject
    private ICayenneService cayenneService;


    @Override
    public Link transformPageRenderLink(Link defaultLink, PageRenderRequestParameters parameters) {
        return defaultLink;
    }

    @Override
    public PageRenderRequestParameters decodePageRenderRequest(Request request) {
        String path = request.getPath();

        Matcher matcher = REGEXP_USI_PATH.matcher(path);
        if (matcher.matches()) {
            matcher = REGEXP_USI_CONTEXT.matcher(matcher.group(1));

            EventContext eventContext = new EmptyEventContext();
            if (matcher.matches())
            {
                final String uniqueCode = matcher.group(0);
                eventContext = new AbstractEventContext() {
                    @Override
                    public int getCount() {
                        return 1;
                    }

                    @Override
                    public <T> T get(Class<T> desiredType, int index) {
                        return (T) uniqueCode;
                    }
                };
            }
            return new PageRenderRequestParameters("usi/usi", eventContext, false);
        }

        return null;
    }
}
