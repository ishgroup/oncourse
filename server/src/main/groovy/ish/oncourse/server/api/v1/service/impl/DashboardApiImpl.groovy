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
import groovy.transform.CompileStatic
import ish.common.types.EnrolmentStatus
import ish.common.types.KeyCode
import ish.common.types.Mask
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.function.DateFunctions
import ish.oncourse.server.security.api.IPermissionService

import static ish.oncourse.server.api.v1.function.DashboardFunctions.toStatisticItem
import ish.oncourse.server.api.v1.model.SearchGroupDTO
import ish.oncourse.server.api.v1.model.StatisticDataDTO
import ish.oncourse.server.api.v1.service.DashboardApi
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.WaitingList
import ish.oncourse.server.dashboard.DashboardSearchManager
import org.apache.cayenne.DataRow
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.Property
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SQLSelect
import org.apache.commons.lang.StringUtils

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response
import java.time.LocalDate

@CompileStatic
class DashboardApiImpl implements DashboardApi {

    private static final String YYYYMMDD = 'yyyyMMdd'
    private static final String YYYY_MM_DD = 'yyyy-MM-dd'
    private static final String NO_PERCENT_SIGN = /[^%]*/

    @Inject
    private ICayenneService cayenneService

    @Inject
    private DashboardSearchManager dashboardSearchManager

    @Inject
    private IPermissionService permissionService

    @Override
    List<SearchGroupDTO> getSearchResults(String search) {
        if (search ==~ NO_PERCENT_SIGN) {
            dashboardSearchManager.getSearchResult(search)
        } else {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity('Cannot use % sign in quick search.').build())
        }
    }

    @Override
    StatisticDataDTO getStatistic() {
        Date today = new Date()

        Date startOfPeriod = LocalDate.now().minusDays(27).atStartOfDay().toDate()
        boolean isUserCanViewIncoice = permissionService.currentUserCan(KeyCode.INVOICE, Mask.VIEW)

        new StatisticDataDTO().with { sd ->

            ObjectContext context = cayenneService.newContext

            Map<String, Integer> enrolmentsCountPerDayMap = getEnrolmentsPerDaysFrom(context, startOfPeriod)
            Map<String, BigDecimal> revenuePerDayMap = getInvoicesRevenuePerDaysFrom(context, startOfPeriod)

            List<Integer> enrolmentValues = (0..27).collect { enrolmentsCountPerDayMap[(startOfPeriod + it).format(YYYYMMDD)]?.intValue() ?: 0 }
            List<BigDecimal> revenueValues = (0..27).collect { revenuePerDayMap[(startOfPeriod + it).format(YYYYMMDD)] ?: 0.0 }

            sd.enrolmentsChartLine = enrolmentValues
            sd.studentsCount = enrolmentsCountPerDayMap.values().sum() as Integer
            if (isUserCanViewIncoice) {
                sd.revenueChartLine = revenueValues
                sd.moneyCount = (revenuePerDayMap.values().sum() as BigDecimal) ?: BigDecimal.ZERO
            }

            sd.latestEnrolments = ObjectSelect.query(Enrolment)
                    .where(Enrolment.STATUS.in(EnrolmentStatus.STATUSES_LEGIT))
                    .orderBy(Enrolment.CREATED_ON.desc())
                    .limit(3)
                    .select(cayenneService.newContext)
                    .collect { toStatisticItem(
                        it.courseClass.course.name,
                        DateFunctions.getTimeAgo(today.time - it.createdOn.time),
                        "/enrolment/$it.id"
            )}

            sd.latestWaitingLists = ObjectSelect.query(WaitingList)
                    .columns(WaitingList.COURSE.dot(Course.NAME), Property.COUNT, WaitingList.COURSE.dot(Course.ID))
                    .where(WaitingList.CREATED_ON.gte(startOfPeriod))
                    .orderBy(Property.COUNT.desc())
                    .limit(3)
                    .select(cayenneService.newContext)
                    .collect { toStatisticItem(
                    it[0] as String,
                    it[1] as String,
                    "/waitingList?search=course.id=${it[2]}"
            )
            }
            sd.openedClasses = getClassesCountWhere(CourseClass.IS_CANCELLED.isFalse()
                    .andExp(CourseClass.END_DATE_TIME.gt(today))
                    .andExp(CourseClass.IS_ACTIVE.isTrue()))
            sd.inDevelopmentClasses = getClassesCountWhere(CourseClass.IS_CANCELLED.isFalse()
                    .andExp(CourseClass.END_DATE_TIME.gt(today))
                    .andExp(CourseClass.IS_ACTIVE.isFalse()))
            sd.cancelledClasses = getClassesCountWhere(CourseClass.IS_CANCELLED.isTrue()
                    .andExp(CourseClass.END_DATE_TIME.gt(today)))
            sd.completedClasses = getClassesCountWhere(CourseClass.END_DATE_TIME.lt(today))
            sd.commencedClasses = getClassesCountWhere(CourseClass.IS_CANCELLED.isFalse()
                    .andExp(CourseClass.END_DATE_TIME.gt(today))
                    .andExp(CourseClass.START_DATE_TIME.lt(today)))
            sd
        }
    }

    private Map<String, Integer> getEnrolmentsPerDaysFrom(ObjectContext context, Date startOfPeriod) {
        List<DataRow> latestEnrolmentsPerDay = SQLSelect.dataRowQuery(String.format("select enr.createdOn enrolmentsDate, count(enr.id) enrolmentsCountPerDay from\n" +
                "(select DATE(e.createdOn) createdOn, id, status FROM Enrolment e) enr\n" +
                "where enr.createdOn >= '%s' and enr.status in (%s)\n" +
                "group by enr.createdOn\n" +
                "order by enr.createdOn asc",
                startOfPeriod.format(YYYY_MM_DD), StringUtils.join(EnrolmentStatus.STATUSES_LEGIT.collect{it.databaseValue}, ","))).select(context)

        Map<String, Integer> enrolmentsCountMapPerDay = latestEnrolmentsPerDay.collectEntries{ e ->
            Date date = e.enrolmentsDate != null ? e.enrolmentsDate as Date : e.ENROLMENTSDATE as Date
            Integer count = e.enrolmentsCountPerDay != null ? e.enrolmentsCountPerDay as Integer : e.ENROLMENTSCOUNTPERDAY as Integer

            [date.format(YYYYMMDD), count]
        }
        enrolmentsCountMapPerDay
    }

    private Map<String, BigDecimal> getInvoicesRevenuePerDaysFrom(ObjectContext context, Date startOfPeriod) {
        List<DataRow> latestRevenueRows = SQLSelect.dataRowQuery(String.format("select orderedInvoices.createdOn invoicesDate, sum(orderedInvoices.invoiceSum) invoicesPerDateRevenue from \n" +
                "(select sum((il.priceEachexTax - il.discountEachexTax + il.taxEach) * " +
                "il.quantity) invoiceSum, DATE(il.createdOn) createdOn from InvoiceLine il\n" +
                "GROUP by il.invoiceId, il.createdOn) orderedInvoices\n" +
                "where orderedInvoices.createdOn >= '%s'\n" +
                "GROUP BY orderedInvoices.createdOn\n" +
                "order by orderedInvoices.createdOn asc", startOfPeriod.format(YYYY_MM_DD))).select(context)

        Map<String, BigDecimal> revenueMapPerDay = latestRevenueRows.collectEntries { e ->
            Date date = e.invoicesDate != null ? e.invoicesDate as Date : e.INVOICESDATE as Date
            BigDecimal revenue = e.invoicesPerDateRevenue != null ?  e.invoicesPerDateRevenue as BigDecimal : e.INVOICESPERDATEREVENUE as BigDecimal

            [date.format(YYYYMMDD), revenue]
        }
        revenueMapPerDay
    }

    private Integer getClassesCountWhere(Expression exp) {
        ObjectSelect.query(CourseClass)
                .where(exp)
                .selectCount(cayenneService.newContext)
    }
}
