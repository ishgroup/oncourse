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

package ish.oncourse.server.services

import javax.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.oncourse.types.AuditAction
import org.apache.cayenne.Cayenne
import org.apache.cayenne.tx.TransactionDescriptor
import org.apache.cayenne.tx.TransactionManager
import org.apache.cayenne.tx.TransactionPropagation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.sql.Types
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@CompileStatic
class AuditService {

    private static final Logger logger = LogManager.logger

    ISystemUserService systemUserService
    ICayenneService cayenneService
    ExecutorService executorService
    private static String INSERT_AUDIT_PATTERN = 'INSERT INTO Audit (created, systemUserId, entityId, entityName, action, message) VALUES (?, ?, ?, ?, ?, ?)'

    @Inject
    AuditService(ICayenneService cayenneService, ISystemUserService systemUserService) {
        this.systemUserService = systemUserService
        this.cayenneService = cayenneService
        executorService = Executors.newSingleThreadExecutor()
    }


    def submit(CayenneDataObject object, AuditAction action) {
        submit(object, action, null)
    }


    def submit(CayenneDataObject object, AuditAction action, String message) {
        if (!object.auditAllowed) {
            return
        }

        def entityId = Cayenne.longPKForObject(object)
        def entityName = object.class.simpleName

        if (message == null) {
            message = defaultMessage(action, object)
        }

        submit(entityName, entityId, action, message)
    }

    def submit(String entityName, Long entityId, AuditAction action, String message){
        def userId = systemUserService.currentUser?.id
        def created = new Timestamp(new Date().time)

        executorService.submit({
            TransactionManager manager = cayenneService.serverRuntime.getInjector().getInstance(TransactionManager.class)
                TransactionDescriptor descriptor = new TransactionDescriptor(
                    Connection.TRANSACTION_SERIALIZABLE, // set transaction isolation to SERIALIZABLE
                    TransactionPropagation.REQUIRES_NEW  // require new transaction for every operation
            )
            manager.performInTransaction({ ->
                Connection connection
                try {
                    connection = cayenneService.dataSource.connection
                    PreparedStatement stmt = connection.prepareStatement(INSERT_AUDIT_PATTERN)
                    stmt.setTimestamp(1, created)

                    setLong(stmt, 2, userId)
                    setLong(stmt, 3, entityId)
                    setString(stmt, 4, entityName)
                    setString(stmt, 5, action.databaseValue)
                    setString(stmt, 6, message)

                    stmt.executeUpdate()
                    connection.commit()
                    stmt.close()
                } catch (Exception e) {
                    logger.warn("Fail to submit audit entry for ${entityId}, action:${action.name()}", e)
                } finally {
                    if (connection != null && !connection.isClosed()) {
                        connection.close()
                    }
                }
            }, descriptor)
        })
    }

    private String defaultMessage(AuditAction action, CayenneDataObject object) {
        return systemUserService.currentUser ?
                action.displayName + " by " + systemUserService.currentUser.email + ": " + object.getSummaryDescription() :
                action.displayName + ": " + object.getSummaryDescription()
    }

    private void setString(PreparedStatement stmt, int index, String value) {
        if (value != null) {
            stmt.setString(index, value)
        } else {
            stmt.setNull(index, Types.VARCHAR)
        }
    }

    private void setLong(PreparedStatement stmt, int index, Long value) {
        if (value != null) {
            stmt.setLong(index, value)
        } else {
            stmt.setNull(index, Types.VARCHAR)
        }
    }
}
