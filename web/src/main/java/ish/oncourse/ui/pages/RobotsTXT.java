/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.pages;

import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSiteLayout;
import ish.oncourse.model.WebTemplate;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Meta;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

@Meta("tapestry.response-content-type=text/plain")
public class RobotsTXT {

    @Inject
    private Request request;

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private IWebSiteVersionService webSiteVersionService;

    private static final String ROBOT_FILE_NAME = "robots.txt";

    private static final String ROBOTS_CONTENT_TYPE = "text/plain";

    private static final String USER_AGENT = "User-agent: *";

    private static final String SITE_MAP_PATTERN = "Sitemap: https://%s/sitemap.xml";

    private static final String DEFAULT_CONTENT =
            "Disallow: /cms/\n" +
            "Disallow: /asset/ \n" +
            "Disallow: /enrol/\n" +
            "Disallow: /addToCookies\n" +
            "Disallow: /ui/courses.showmorecourses\n" +
            "Disallow: /Timeline/\n" +
            "Disallow: /site/kiosk/\n" +
            "Disallow: /room/kiosk/";

    private static final String ROBOTS_TXT_PATTERN = "%s\n%s\n%s";

    StreamResponse onActivate() {

        WebTemplate robotTemplate = ObjectSelect.query(WebTemplate.class)
                .where(WebTemplate.NAME.eq(ROBOT_FILE_NAME))
                .and(WebTemplate.LAYOUT.dot(WebSiteLayout.LAYOUT_KEY).eq(WebNodeType.DEFAULT_LAYOUT_KEY))
                .and(WebTemplate.LAYOUT.dot(WebSiteLayout.WEB_SITE_VERSION).eq(webSiteVersionService.getCurrentVersion()))
                .selectOne(cayenneService.newContext());

        String content = robotTemplate != null && StringUtils.trimToNull(robotTemplate.getContent()) != null ?  robotTemplate.getContent() : DEFAULT_CONTENT;
        String siteMap = String.format(SITE_MAP_PATTERN, request.getServerName());
        String robotTxt = String.format(ROBOTS_TXT_PATTERN, USER_AGENT, content, siteMap);

        return new TextStreamResponse(ROBOTS_CONTENT_TYPE, robotTxt);
    }
    
}
