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

package ish.oncourse.server.api.v1.service.impl

import com.google.inject.Inject
import ish.oncourse.aql.AqlService
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.dao.SessionDao
import static ish.oncourse.server.api.function.EntityFunctions.addAqlExp
import static ish.oncourse.server.api.v1.function.TimetableFunctions.getDateRangeExpression
import static ish.oncourse.server.api.v1.function.TimetableFunctions.toRestSession
import ish.oncourse.server.api.v1.model.SearchRequestDTO
import ish.oncourse.server.api.v1.model.SessionDTO
import ish.oncourse.server.api.v1.service.TimetableApi
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Room
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.Tutor
import static ish.util.LocalDateUtils.dateToTimeValue
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.FunctionExpressionFactory
import org.apache.cayenne.exp.Property
import org.apache.cayenne.query.ObjectSelect

class TimetableApiImpl implements TimetableApi {

    @Inject
    private ICayenneService cayenneService

    @Inject
    private AqlService aql

    @Inject
    private SessionDao sessionDao

    void setCayenneService(ICayenneService cayenneService) {
        this.cayenneService = cayenneService
    }

    void setAql(AqlService aql) {
        this.aql = aql
    }


    @Override
    List<SessionDTO> find(SearchRequestDTO request) {

        ObjectContext context = cayenneService.newReadonlyContext
        Expression dateFilter = getDateRangeExpression(request.from,request.to)

        ObjectSelect query = ObjectSelect.query(Session).where(dateFilter) &
                Session.COURSE_CLASS.dot(CourseClass.IS_CANCELLED).isFalse()

        query = addAqlExp(request.search, Session, context, query, aql)
        query = query.orderBy(Session.START_DATETIME.asc(), Session.ID.asc())
        return query.columns(Session.ID, Session.START_DATETIME, Session.END_DATETIME)
                .select(context)
                .collect {
                    new SessionDTO(id: it[0] as Long, start: dateToTimeValue(it[1] as Date), end:  dateToTimeValue(it[2] as Date))
                }
    }

    @Override
    List<SessionDTO> get(String idsParam) {
        if(idsParam == null || idsParam.isEmpty()) {
            return Collections.emptyList()
        }
        List<Long> ids = idsParam.split(',').collect {Long.valueOf(it)}
        fetchSessions(Session.ID.in(ids))
                .collect {toRestSession(it)}
    }

    @Override
    List<Integer> getDates(Integer month, Integer year, String search) {
        ObjectContext context = cayenneService.newReadonlyContext

        Date startOfMonth = new GregorianCalendar(year, month, 1).time
        Date endOfMonth = (month == Calendar.DECEMBER ? new GregorianCalendar(++year, 0, 1): new GregorianCalendar(year, ++month, 1)).time

        ObjectSelect query = ObjectSelect.query(Session)
                .where(Session.START_DATETIME.between(startOfMonth, endOfMonth)) &
                Session.COURSE_CLASS.dot(CourseClass.IS_CANCELLED).isFalse()


        query = addAqlExp(search, Session, context, query, aql)

        Property<Integer> dayOfMonth = Property
                .create(FunctionExpressionFactory.dayOfMonthExp(Session.START_DATETIME.path()), Integer.class)

        query.columns(dayOfMonth, Property.COUNT).select(context).collect{it[0] as Integer}

    }

    @Override
    List<SessionDTO> getForClasses(String classIds) {
        List<Long> ids = classIds.split(',').collect {Long.valueOf(it)}
        fetchSessions(Session.COURSE_CLASS.dot(CourseClass.ID).in(ids))
                .collect {toRestSession(it)}
    }

    @Override
    List<Map<String, String>> getSessionsTags(String sessionsIds) {
        if (sessionsIds == null || sessionsIds.isEmpty()) {
            return Collections.emptyList()
        }
        List<Long> ids = sessionsIds.split(',').collect { Long.valueOf(it) }
        sessionDao.getTimetableTags(cayenneService.newReadonlyContext, ids)
    }

    private List<Session> fetchSessions(Expression qualifier) {
        ObjectContext context = cayenneService.newReadonlyContext
        return ObjectSelect.query(Session)
                .where(qualifier)
                .prefetch(Session.ROOM.joint())
                .prefetch(Session.ROOM.dot(Room.SITE).joint())
                .prefetch(Session.TUTORS.disjointById())
                .prefetch(Session.TUTORS.dot(Tutor.CONTACT).joint())
                .orderBy(Session.START_DATETIME.asc(), Session.ID.asc())
                .select(context)
    }
}
