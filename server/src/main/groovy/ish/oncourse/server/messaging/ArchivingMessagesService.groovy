/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.messaging

import com.google.inject.Inject
import com.opencsv.CSVWriter
import com.opencsv.CSVWriterBuilder
import com.opencsv.ICSVWriter
import com.opencsv.ResultSetHelperService
import ish.oncourse.server.ICayenneService

import java.sql.ResultSet
import java.text.SimpleDateFormat
import java.util.zip.GZIPOutputStream

class ArchivingMessagesService {

    @Inject
    ICayenneService cayenneService


    void buildCsvFrom(Date date) {
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(new FileOutputStream("output.csv.gz"))
        PrintWriter printWriter = new PrintWriter(gzipOutputStream)
        /*ResultSetHelperService service = new ResultSetHelperService();
        service.setDateFormat("mm/dd/yy");*/
        ICSVWriter csvWriter = new CSVWriterBuilder(printWriter)
                //.withResultSetHelper(service)
                .build()

        def dateFormat = new SimpleDateFormat("yyyy-MM-dd")

        def dataSource = cayenneService.getDataSource()
        def connection = dataSource.getConnection()
        def statement = connection.createStatement()
        def startId = 0L
        ResultSet resultSet = null
        int iterations = 0
        int lastFetchSize = 0
        while (resultSet == null || lastFetchSize > 0 && iterations < 5){
            resultSet = statement.executeQuery("Select * from Message m WHERE m.id>${startId} and m.createdOn < '${dateFormat.format(date)}' ORDER BY m.id LIMIT 500")
            csvWriter.writeAll(resultSet, resultSet == null)
            resultSet.last()
            startId = resultSet.getLong("id")
            lastFetchSize = resultSet.getRow()
            iterations++
        }
        statement.close()
        connection.close()
        gzipOutputStream.close()
    }
}
