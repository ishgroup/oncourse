/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.messaging

import com.google.inject.Inject
import com.opencsv.CSVWriterBuilder
import com.opencsv.ICSVWriter
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.license.LicenseService

import java.sql.ResultSet
import java.text.SimpleDateFormat
import java.util.zip.GZIPOutputStream

class ArchivingMessagesService {

    @Inject
    ICayenneService cayenneService

    @Inject
    LicenseService licenseService


    void buildCompressedCsvForMessagesBefore(Date date) {
        println "Start time: " + System.currentTimeMillis()
        def collegeName = licenseService.getCollege_key()
        def year = date.toInstant().toCalendar().toYear().value
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(new FileOutputStream("$collegeName-$year" + ".csv.gz"))
        PrintWriter printWriter = new PrintWriter(gzipOutputStream)
        ICSVWriter csvWriter = new CSVWriterBuilder(printWriter)
                .build()

        def dateFormat = new SimpleDateFormat("yyyy-MM-dd")

        def dataSource = cayenneService.getDataSource()
        def connection = dataSource.getConnection()
        def statement = connection.createStatement()
        def startId = 0L
        ResultSet resultSet = null
        int iterations = 0
        int lastFetchSize = 0
        while (resultSet == null || lastFetchSize > 0) {
            resultSet = statement.executeQuery("Select * from Message m WHERE m.id>${startId} and m.createdOn < '${dateFormat.format(date)}' ORDER BY m.id LIMIT 500")
            csvWriter.writeAll(resultSet, resultSet == null)
            resultSet.last()
            lastFetchSize = resultSet.getRow()
            if (lastFetchSize > 0) {
                startId = resultSet.getLong("id")
            }
            iterations++
        }
        statement.close()
        connection.close()
        gzipOutputStream.close()
        println "End time: " + System.currentTimeMillis()
    }
}
