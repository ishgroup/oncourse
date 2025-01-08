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

import javax.inject.Inject
import ish.common.types.NodeType
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.SessionModule
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils
import ish.oncourse.server.cayenne.Session
import org.apache.cayenne.DataRow
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SQLSelect
import org.apache.cayenne.query.SelectById

class SessionDao implements ClassRelatedDao<Session> {


    @Inject
    private AttendanceDao attendanceDao

    private final static String SESSION_RELATED_TAGS_SQL_PATTERN = "select distinct cs.id as SESSIONID, n.name as NAME, n.colour as COLOUR from CourseSession cs\n" +
            "left join CourseClass cc on cc.id = cs.courseClass_Id\n" +
            "left join Course c on c.id = cc.courseId\n" +
            "left join Room r on r.id = cc.roomId\n" +
            "left join Site s on s.id = r.siteId\n" +
            "left join NodeRelation nr on nr.entityRecordId = cc.id or nr.entityRecordId = c.id or nr.entityRecordId = r.id or nr.entityRecordId = s.id\n" +
            "left join Node n on nr.nodeId = n.id\n" +
            "where cs.id in (%s) AND n.nodeType = " + NodeType.TAG.databaseValue

    @Override
    Session newObject(ObjectContext context) {
        context.newObject(Session)
    }

    Session newObject(ObjectContext context, CourseClass courseClass) {
        Session session = newObject(context)
        if (courseClass) {
            session.courseClass = courseClass

            courseClass.successAndQueuedEnrolments.each { e ->
                attendanceDao.newObject(context, session, e.student)
            }

            courseClass.course.modules.each { module ->
                SessionModule sessionModule = context.newObject(SessionModule)
                sessionModule.session = session
                sessionModule.module = module
            }
        }

        session
    }

    @Override
    Session getById(ObjectContext context, Long id) {
        SelectById.query(Session, id)
                .selectOne(context)
    }

    List<Map<String, String>> getTimetableTags(ObjectContext context, List<Long> sessionIds) {
        List<DataRow> resultSet = SQLSelect.dataRowQuery(String.format(SESSION_RELATED_TAGS_SQL_PATTERN, StringUtils.join(sessionIds, ','))).select(context)

        List<Map<String, String>> result = new ArrayList<>()
        sessionIds.each {result.add(new HashMap<String, String>())}

        for (DataRow r : resultSet) {
            int index = sessionIds.toArray().findIndexOf {it == (long) r.get("SESSIONID")}
            if (r.get("NAME") != null) {
                result.get(index).put((String) r.get("NAME"), (String) r.get("COLOUR"))
            }
        }
        result
    }

    @Override
    List<Session> getByClassId(ObjectContext context, Long courseClassId) {
        ObjectSelect.query(Session).where(Session.COURSE_CLASS.dot(CourseClass.ID)
                .eq(courseClassId))
                .orderBy(Session.START_DATETIME.asc())
                .select(context)
    }
}
