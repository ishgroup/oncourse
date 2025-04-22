/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import ish.common.types.EnrolmentStatus
import ish.math.Money
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.Room
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.Site
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SQLSelect

import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

def enrolment = ObjectSelect.query(Enrolment).selectFirst(context)


def firstDayOfMonth3 = LocalDate.of((int) yearToCheck, (int) monthToCheck, 1)

LocalDate firstDayOfMonth2 = firstDayOfMonth3
        .plusMonths(-1)
        .withDayOfMonth(1)

LocalDate firstDayOfMonth1 = firstDayOfMonth3.plusMonths(-2)
        .withDayOfMonth(1)

LocalDate firstDayOfMonth4 = firstDayOfMonth3.plusMonths(1)
        .withDayOfMonth(1)

LocalDate yearToDate = firstDayOfMonth3.withMonth(1)

LocalDate previousYearStart = yearToDate.minusYears(1)
LocalDate previousYearIntervalEnd = firstDayOfMonth4.minusYears(1)


def dataForIntervals = new ArrayList<Map<String, Object>>()
dataForIntervals.add(buildDataForInterval("month1", firstDayOfMonth1, firstDayOfMonth2, context))
dataForIntervals.add(buildDataForInterval("month2", firstDayOfMonth2, firstDayOfMonth3, context))
dataForIntervals.add(buildDataForInterval("month3", firstDayOfMonth3, firstDayOfMonth4, context))
dataForIntervals.add(buildDataForInterval("yearToDate", yearToDate, firstDayOfMonth4, context))
dataForIntervals.add(buildDataForInterval("lastYear", previousYearStart, previousYearIntervalEnd, context))


def result = new ArrayList<Map<String, Object>>();

dataForIntervals.first().each { key, value ->
    if (!key.equals("key")) {
        Map<String, Object> map = new HashMap<>()
        map.put("rowName", key)
        result.add(map)
    }
}


result.each { resultMap ->
    def column = resultMap.get("rowName")
    dataForIntervals.each { intervalMap ->
        resultMap.put(intervalMap["key"] as String, intervalMap.get(column))
    }
}

report {
    keycode "ish.onCourse.Enrolment.enrolmentSummary"
    records Arrays.asList(enrolment)
    param 'entitiesToPrint': result
}


Map<String, Object> buildDataForInterval(String key, LocalDate startDate, LocalDate excludeEndDate, ObjectContext context) {
    def result = new LinkedHashMap<String, Object>();

    result.put("key", key)

    def enrolmentsCount = ObjectSelect.query(Enrolment)
            .where(Enrolment.CREATED_ON.gte(startDate.toDate()).andExp(Enrolment.CREATED_ON.lt(excludeEndDate.toDate()))
                    .andExp(Enrolment.STATUS.in(EnrolmentStatus.STATUSES_LEGIT)))
            .selectCount(context)

    result.put("Enrolments", enrolmentsCount)

    def startDateStr = startDate.format("yyyy-MM-dd HH:mm:ss")
    def endDateStr = excludeEndDate.format("yyyy-MM-dd HH:mm:ss")

    def studentsCount = SQLSelect.dataRowQuery(("Select count(distinct s.id) as s_count " +
            "from Enrolment e JOIN Student s ON e.studentId = s.id " +
            "WHERE e.createdOn >= '$startDateStr' and e.createdOn < '$endDateStr'"))
            .selectOne(context).get("s_count")

    result.put("Students", studentsCount)


    def invoicesIterator = ObjectSelect.query(Invoice).where(
            Invoice.CREATED_ON.lt(excludeEndDate.toDate())
                    .andExp(Invoice.CREATED_ON.gte(startDate.toDate())))
            .batchIterator(context, 200)


    Money invoicesSum = Money.ZERO

    try {
        invoicesIterator.each { batch ->
            batch.each {
                invoicesSum = invoicesSum.add(it.totalIncTax)
            }
        }
    } finally {
        invoicesIterator.close()
    }

    result.put("Income", invoicesSum.toPlainString())


    long classesPlaces = 0
    long classesEnrolments = 0

    def classesIterator = ObjectSelect.query(CourseClass).where(
            CourseClass.ENROLMENTS.dot(Enrolment.STATUS).in(EnrolmentStatus.STATUSES_LEGIT)
                    .andExp(CourseClass.ENROLMENTS.dot(Enrolment.CREATED_ON).lt(excludeEndDate.toDate()))
                    .andExp(CourseClass.ENROLMENTS.dot(Enrolment.CREATED_ON).gte(startDate.toDate()))
    ).batchIterator(context, 200)

    try {
        classesIterator.each { batch ->
            batch.each { cc ->
                classesPlaces += cc.maximumPlaces
                classesEnrolments += cc.actualEnrolmentCount
            }
        }
    } finally {
        classesIterator.close()
    }

    double classesPercentage = (classesPlaces == 0 ? 0 : (double) classesEnrolments / classesPlaces) * 100
    DecimalFormat df = new DecimalFormat("0.00")

    result.put("Course capacity", df.format(classesPercentage) + "%")

    def defaultSessionsExpression = Session.START_DATETIME.gte(startDate.toDate()).andExp(Session.START_DATETIME.lt(excludeEndDate.toDate()))
            .orExp(Session.END_DATETIME.gte(startDate.toDate()).andExp(Session.END_DATETIME.lt(excludeEndDate.toDate())))

    if(!siteNameToCheck?.isBlank()) {
        defaultSessionsExpression = defaultSessionsExpression.andExp(Session.ROOM.dot(Room.SITE).dot(Site.NAME).eq(siteNameToCheck))
    }

    def sessions = ObjectSelect.query(Session)
            .where(defaultSessionsExpression)
            .batchIterator(context, 500)

    def nineThirty = 9 * 60 + 30
    def elevenThirty = 11 * 60 + 30
    def thirteenThirty = 13 * 60 + 30
    def fifteenThirty = 15 * 60 + 30
    def seventeenThirty = 17 * 60 + 30
    def nineteenThirty = 19 * 60 + 30

    int slotOneCount = 0, slotTwoCount = 0, slotThreeCount = 0, slotFourCount = 0, slotFiveCount = 0

    try {
        sessions.each { batch ->
            batch.each { session ->
                def localStartTime = LocalDateTime.ofInstant(session.startDatetime.toInstant(), session.timeZone.toZoneId())
                def localEndTime = LocalDateTime.ofInstant(session.endDatetime.toInstant(), session.timeZone.toZoneId())

                def startTimeMinute = localStartTime.hour * 60 + localStartTime.minute
                def endTimeMinute = localEndTime.hour * 60 + localEndTime.minute


                if (intersects(nineThirty, elevenThirty, startTimeMinute, endTimeMinute))
                    slotOneCount++
                if (intersects(elevenThirty, thirteenThirty, startTimeMinute, endTimeMinute))
                    slotTwoCount++
                if (intersects(thirteenThirty, fifteenThirty, startTimeMinute, endTimeMinute))
                    slotThreeCount++
                if (intersects(fifteenThirty, seventeenThirty, startTimeMinute, endTimeMinute))
                    slotFourCount++
                if (intersects(seventeenThirty, nineteenThirty, startTimeMinute, endTimeMinute))
                    slotFiveCount++
            }
        }
    } finally {
        sessions.close()
    }

    def daysBetween = ChronoUnit.DAYS.between(startDate, excludeEndDate)
    if(daysBetween == 0)
        daysBetween = 1

    double roomsPercentage = (double) (slotOneCount + slotTwoCount + slotThreeCount + slotFourCount + slotFiveCount) / 5 / daysBetween * 100;
    result.put("Room capacity", df.format(roomsPercentage) + "%")


    Expression classesQuery = ExpressionFactory.and(CourseClass.START_DATE_TIME.gte(startDate.toDate())
            .andExp(CourseClass.START_DATE_TIME.lt(excludeEndDate.toDate()))
            .orExp(CourseClass.END_DATE_TIME.gte(startDate.toDate())
                    .andExp(CourseClass.END_DATE_TIME.lt(excludeEndDate.toDate()))),
            CourseClass.IS_CANCELLED.isFalse())

    def classesCount = 0L
    def classesCountIterator = ObjectSelect.query(CourseClass)
            .where(classesQuery)
            .batchIterator(context, 300)

    try {
        classesCountIterator.each {
            classesCount += it.findAll { it.enrolments.size() > 0 }.size()
        }
    } finally {
        classesCountIterator.close()
    }

    result.put("Online / External Courses", classesCount)

    Expression cancelledClassesQuery = ExpressionFactory.and(CourseClass.START_DATE_TIME.gte(startDate.toDate())
            .andExp(CourseClass.START_DATE_TIME.lt(excludeEndDate.toDate()))
            .orExp(CourseClass.END_DATE_TIME.gte(startDate.toDate())
                    .andExp(CourseClass.END_DATE_TIME.lt(excludeEndDate.toDate()))),
            CourseClass.IS_CANCELLED.isTrue())

    def cancelledClassesCount = ObjectSelect.query(CourseClass)
            .where(cancelledClassesQuery)
            .selectCount(context)

    result.put("Cancellations", cancelledClassesCount)

    return result
}


private static boolean intersects(int firstStart, int firstEnd, int secondStart, int secondEnd) {
    return secondStart > firstStart && secondStart < firstEnd || secondEnd > firstStart && secondEnd < firstEnd || secondStart < firstStart && secondEnd > firstEnd || secondStart == firstStart
}