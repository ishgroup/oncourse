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

package ish.oncourse.server.upgrades.liquibase.change;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class IshTaskChangeUtils {

    public static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

    public static List<String> getDeleteQueryBatch(String tableName, List<Long> ids, int batchSize) {
        List<String> queries = new ArrayList<>();
        for (var i = 0; ids.size() - i * batchSize > 0; i++) {
            var query = new StringBuilder();
            query.append("DELETE FROM ");
            query.append(tableName);
            query.append(" WHERE id IN (");
            var startSublistIndex = i * batchSize;
            var endSublistIndex = Math.min((i + 1) * batchSize, ids.size());
            query.append(StringUtils.join(ids.subList(startSublistIndex, endSublistIndex), ","));
            query.append(")");
            queries.add(query.toString());
        }
        return queries;
    }

    public static List<String> getDeleteQueuedRecordsBatch(String willowTableName, List<Long> ids, int batchSize,  Date date ) {
        List<String> result = new ArrayList<>();
        for (var i = 0; ids.size() - i * batchSize > 0; i++) {
            var startSublistIndex = i * batchSize;
            var endSublistIndex = Math.min((i + 1) * batchSize, ids.size());

            var currentDateString = new SimpleDateFormat(DATE_FORMAT).format(date);
            var transactionKey = String.format("remove_transaction_%d-%d_%s", startSublistIndex, endSublistIndex, UUID.randomUUID().toString());
            result.add(String.format("INSERT INTO QueuedTransaction (transactionKey, createdOn, modifiedOn) values ('%s', '%s', '%s')", transactionKey, currentDateString, currentDateString));
            var queuedInsert = new StringBuilder();
            queuedInsert.append("INSERT INTO QueuedRecord ");
            queuedInsert.append("(foreignRecordId, tableName, action, createdOn, modifiedOn, numberOfAttempts, transactionId) ");
            queuedInsert.append("VALUES\n");
            for (var j = startSublistIndex; j < endSublistIndex; j++) {
                queuedInsert.append('(');
                queuedInsert.append(ids.get(j));
                queuedInsert.append(",'");
                queuedInsert.append(willowTableName);
                queuedInsert.append("','DELETE','");
                queuedInsert.append(currentDateString);
                queuedInsert.append("','");
                queuedInsert.append(currentDateString);
                queuedInsert.append("',0,");
                queuedInsert.append(String.format("(SELECT id FROM QueuedTransaction WHERE transactionKey = '%s')", transactionKey));
                queuedInsert.append(")");
                if (j < endSublistIndex - 1) {
                    queuedInsert.append(",\n");
                }
            }
            result.add(queuedInsert.toString());
        }
        return result;
    }
}
