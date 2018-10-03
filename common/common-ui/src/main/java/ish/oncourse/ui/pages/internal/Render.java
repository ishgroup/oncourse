/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.pages.internal;

import ish.oncourse.model.CourseClass;
import ish.oncourse.services.persistence.ICayenneService;

import org.apache.cayenne.query.SelectById;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import static ish.oncourse.solr.ReindexConstants.*;



public class Render {
    
    @Property
    private CourseClass courseClass;

    @Inject
    private ICayenneService cayenneService;

    @Inject
    private Request request;
    


    void onActivate() {

        String component = request.getParameter(PARAM_COMPONENT);
        String id = request.getParameter(PARAM_ID);
        
        if (component != null && id != null) {
            switch (component) {
                case CLASS_COMPONENT:
                    courseClass = SelectById.query(CourseClass.class, id)
                            .prefetch(CourseClass.ROOM.joint())
                            .prefetch(CourseClass.SESSIONS.joint())
                            .prefetch(CourseClass.TUTOR_ROLES.joint())
                            .selectOne(cayenneService.sharedContext());
                    break;
                    default: throw new IllegalArgumentException("Unsupported component:" + component);
            }
        }
    }
}
