/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services.chargebee

import com.google.inject.Inject
import ish.oncourse.server.ICayenneService
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException

@DisallowConcurrentExecution
class ChargebeeUploadJob implements Job {
    private static final int SMS_LENGTH = 160


    @Inject
    private ICayenneService cayenneService

    @Inject
    private ChargebeeService chargebeeService

    @Override
    void execute(JobExecutionContext context) throws JobExecutionException {
        def connection = cayenneService.getDataSource().connection
        def statement = connection.createStatement()

        Calendar aCalendar = Calendar.getInstance()
        aCalendar.add(Calendar.MONTH, -1)
        aCalendar.set(Calendar.DATE, 1)

        Date firstDateOfPreviousMonth = aCalendar.getTime()

        aCalendar.add(Calendar.MONTH, 1)
        aCalendar.set(Calendar.DATE, 1)

        Date firstDateOfCurrentMonth = aCalendar.getTime()


        def resultSet = statement.executeQuery("SELECT SUM(pi.amount) AS value" +
                "          FROM PaymentIn pi" +
                "          WHERE pi.created >= $firstDateOfPreviousMonth" +
                "          AND pi.created <= $firstDateOfCurrentMonth" +
                "          AND pi.type = 2" +
                "          AND pi.status IN (3, 6)")
        def paymentAmount =  resultSet.getLong(0)
        chargebeeService.uploadUsage(ChargebeeItemType.PAYMENT, String.valueOf(paymentAmount))

        def smsResultSet = statement.executeQuery("SELECT COALESCE(SUM((length(m.smsText) DIV $SMS_LENGTH) + 1), 0) AS credits\n" +
                "          FROM Message m" +
                "          WHERE m.smsText IS NOT NULL AND m.smsText <> ''" +
                "          AND m.created >= $firstDateOfPreviousMonth" +
                "          AND m.created <= $firstDateOfCurrentMonth")
        def smsAmount = resultSet.getLong(0)
        chargebeeService.uploadUsage(ChargebeeItemType.SMS, String.valueOf(smsAmount))
    }
}
