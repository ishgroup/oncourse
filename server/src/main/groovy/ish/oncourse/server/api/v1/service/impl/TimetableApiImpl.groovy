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

import javax.inject.Inject
import ish.oncourse.aql.AqlService
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.dao.SessionDao
import ish.oncourse.server.api.v1.model.SearchRequestDTO
import ish.oncourse.server.api.v1.model.SessionDTO
import ish.oncourse.server.api.v1.service.TimetableApi
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Room
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.Tutor
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.util.DurationFormatter
import ish.util.EntityUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.FunctionExpressionFactory
import org.apache.cayenne.exp.Property
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang.ArrayUtils

import java.math.RoundingMode

import static ish.oncourse.server.api.function.EntityFunctions.parseSearchQuery
import static ish.oncourse.server.api.v1.function.TimetableFunctions.getDateRangeExpression
import static ish.oncourse.server.api.v1.function.TimetableFunctions.toRestSession
import static ish.util.LocalDateUtils.dateToTimeValue

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
        Expression dateFilter = getDateRangeExpression(request.from, request.to)

        Class<? extends CayenneDataObject> clzz = EntityUtil.entityClassForName(Session.simpleName)
        ObjectSelect objectSelect = ObjectSelect.query(clzz)
        def query = parseSearchQuery(objectSelect, context, aql, null, request.search, request.filter, null)

        query = query.where(dateFilter) &
                Session.COURSE_CLASS.dot(CourseClass.IS_CANCELLED).isFalse()

        query = query.orderBy(Session.START_DATETIME.asc(), Session.ID.asc())
        return query.columns(Session.ID, Session.START_DATETIME, Session.END_DATETIME)
                .select(context)
                .collect {
                    new SessionDTO(id: it[0] as Long, start: dateToTimeValue(it[1] as Date), end: dateToTimeValue(it[2] as Date))
                }
    }

    @Override
    List<SessionDTO> get(String idsParam) {
        if (idsParam == null || idsParam.isEmpty()) {
            return Collections.emptyList()
        }
        List<Long> ids = idsParam.split(',').collect { Long.valueOf(it) }
        fetchSessions(Session.ID.in(ids))
                .collect { toRestSession(it) }
    }

    @Override
    List<Double> getDates(Integer month, Integer year, SearchRequestDTO request) {
        ObjectContext context = cayenneService.newReadonlyContext

        def calendar = new GregorianCalendar(year, month, 1)
        int monthSize = calendar.toYearMonth().lengthOfMonth()

        Date startOfMonth = calendar.time
        Date endOfMonth = (month == Calendar.DECEMBER ? new GregorianCalendar(++year, 0, 1) : new GregorianCalendar(year, ++month, 1)).time

        Class<? extends CayenneDataObject> clzz = EntityUtil.entityClassForName(Session.simpleName)
        ObjectSelect objectSelect = ObjectSelect.query(clzz)
        def query = parseSearchQuery(objectSelect, context, aql, null, request?.search, request?.filter, null)

        query = query.where(Session.START_DATETIME.between(startOfMonth, endOfMonth)) &
                Session.COURSE_CLASS.dot(CourseClass.IS_CANCELLED).isFalse()


        Property<Integer> dayOfMonth = Property
                .create(FunctionExpressionFactory.dayOfMonthExp(Session.START_DATETIME.path()), Integer.class)

        double[] result = new double[monthSize]
        def queryResult = query.columns(dayOfMonth, Property.COUNT, Session.START_DATETIME, Session.END_DATETIME).select(context)

        queryResult.each { sessionLine ->
            result[(sessionLine[0] as Integer) - 1] += (sessionLine[1] as Integer) * DurationFormatter.durationInHoursBetween(sessionLine[2] as Date, sessionLine[3] as Date).doubleValue()
        }

        def resultAsObj = ArrayUtils.toObject(result)
        def maxHours = resultAsObj.collect { it }.max()
        resultAsObj.collect {
            maxHours == 0 ? 0 :
                    new BigDecimal(it / maxHours.doubleValue()).setScale(2, RoundingMode.HALF_UP).doubleValue()
        }
    }

    @Override
    List<SessionDTO> getForClasses(String classIds) {
        List<Long> ids = classIds.split(',').collect { Long.valueOf(it) }
        fetchSessions(Session.COURSE_CLASS.dot(CourseClass.ID).in(ids))
                .collect { toRestSession(it) }
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
