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

import com.google.inject.Inject
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
import java.util.concurrent.TimeUnit

@CompileStatic
class AuditService {

    private static final Logger logger = LogManager.logger

    ISystemUserService systemUserService
    ICayenneService cayenneService
    ExecutorService executorService
    private static String INSERT_AUDIT_PATTERN = 'INSERT INTO Audit (created, systemUserId, entityId, entityName, action, message, ' +
            'source, requestText, changeSetRef, beforeValues, afterValues) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)'

    @Inject
    AuditService(ICayenneService cayenneService, ISystemUserService systemUserService) {
        this.systemUserService = systemUserService
        this.cayenneService = cayenneService
        executorService = Executors.newSingleThreadExecutor()
    }

    /**
     * Корректно останавливает внутренний однопоточный executor.
     * <p>
     * {@code Executors.newSingleThreadExecutor()} создаёт НЕ-daemon поток, который сам по себе
     * никогда не завершается и удерживает JVM живой. В CI это видно как
     * "Terminate orphan process: pid (...) (java)" после падения сборки.
     * <p>
     * Метод идемпотентен: повторные вызовы безопасны, что важно и для тестов, и для случая,
     * когда shutdown вызывается из нескольких точек завершения приложения.
     * Уже отправленные задачи аудита получают шанс доработать (grace-период), после чего
     * executor останавливается принудительно.
     */
    void shutdown() {
        if (executorService == null || executorService.isShutdown()) {
            return
        }
        executorService.shutdown()
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                logger.warn('Audit executor did not finish pending tasks in time, forcing shutdown.')
                executorService.shutdownNow()
            }
        } catch (InterruptedException e) {
            logger.warn('Interrupted while waiting for audit executor to stop, forcing shutdown.', e)
            executorService.shutdownNow()
            Thread.currentThread().interrupt()
        }
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

        Map<String, Object> before = null
        Map<String, Object> after = null
        if (AuditContext.current()?.recordValues) {
            if (action == AuditAction.CREATE) {
                after = AuditValues.snapshot(object, cayenneService.sharedContext.entityResolver)
            } else if (action == AuditAction.DELETE) {
                before = AuditValues.snapshot(object, cayenneService.sharedContext.entityResolver)
            }
        }

        submit(entityName, entityId, action, message, before, after)
    }

    def submit(String entityName, Long entityId, AuditAction action, String message) {
        submit(entityName, entityId, action, message, null, null)
    }

    /**
     * Inserts an audit entry asynchronously. Source, request text and change set come from the {@link AuditContext}
     * open on the calling thread.
     *
     * @param before values before the change, {property: value}, see {@link AuditValues} for how they are stored
     * @param after values after the change
     */
    def submit(String entityName, Long entityId, AuditAction action, String message, Map<String, ?> before, Map<String, ?> after) {
        def userId = systemUserService.currentUser?.id
        def created = new Timestamp(new Date().time)
        // the context and the values belong to the calling thread, read them before going async
        AuditContext context = AuditContext.current()
        String source = context?.source?.databaseValue
        String requestText = context?.requestText
        String changeSetRef = context?.changeSetRef
        String beforeValues = AuditValues.toJson(entityName, before)
        String afterValues = AuditValues.toJson(entityName, after)

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
                    setString(stmt, 7, source)
                    setString(stmt, 8, requestText)
                    setString(stmt, 9, changeSetRef)
                    setString(stmt, 10, beforeValues)
                    setString(stmt, 11, afterValues)

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
