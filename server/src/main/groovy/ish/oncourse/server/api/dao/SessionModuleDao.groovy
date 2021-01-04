/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.dao

import ish.oncourse.server.cayenne.Attendance
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.SessionModule
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

class SessionModuleDao implements ClassRelatedDao<SessionModule> {

    @Override
    List<SessionModule> getByClassId(ObjectContext context, Long courseClassId) {
        return ObjectSelect.query(SessionModule)
                .where(SessionModule.SESSION.dot(Session.COURSE_CLASS).dot(CourseClass.ID).eq(courseClassId))
                .prefetch(SessionModule.SESSION.joint())
                .prefetch(SessionModule.MODULE.joint())
                .select(context)
    }

    List<SessionModule> getTrainingPlan(ObjectContext context, CourseClass courseClass, ish.oncourse.server.cayenne.Module module) {
        return ObjectSelect.query(SessionModule)
                .where(SessionModule.SESSION.dot(Session.COURSE_CLASS).eq(courseClass))
                .and(SessionModule.MODULE.eq(module))
                .orderBy(SessionModule.SESSION.dot(Session.ID).asc())
                .select(context)
    }

    SessionModule newObject(ObjectContext context, Session session, ish.oncourse.server.cayenne.Module module) {
        SessionModule sessionModule = newObject(context)
        sessionModule.session = session
        sessionModule.module = module
        sessionModule
    }

    @Override
    SessionModule newObject(ObjectContext context) {
        return context.newObject(SessionModule)
    }

    @Override
    SessionModule getById(ObjectContext context, Long id) {
        throw new UnsupportedOperationException()
    }
}
