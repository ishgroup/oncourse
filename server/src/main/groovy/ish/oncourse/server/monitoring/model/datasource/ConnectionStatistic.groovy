/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.monitoring.model.datasource

class ConnectionStatistic {

    private Integer totalConnections
    private Integer activeConnections
    private Integer idleConnections
    private Integer threadsAwaitingConnection

    ConnectionStatistic(Integer totalConnections, Integer activeConnections, Integer idleConnections, Integer threadsAwaitingConnection) {
        this.totalConnections = totalConnections
        this.activeConnections = activeConnections
        this.idleConnections = idleConnections
        this.threadsAwaitingConnection = threadsAwaitingConnection
    }

    Integer getTotalConnections() {
        return totalConnections
    }

    Integer getActiveConnections() {
        return activeConnections
    }

    Integer getIdleConnections() {
        return idleConnections
    }

    Integer getThreadsAwaitingConnection() {
        return threadsAwaitingConnection
    }

    static class Builder {

        private Integer totalConnections = null
        private Integer activeConnections = null
        private Integer idleConnections = null
        private Integer threadsAwaitingConnection = null

        Builder setTotalConnections(Integer totalConnections) {
            this.totalConnections = totalConnections
            return this
        }

        Builder setActiveConnections(Integer activeConnections) {
            this.activeConnections = activeConnections
            return this
        }

        Builder setIdleConnections(Integer idleConnections) {
            this.idleConnections = idleConnections
            return this
        }

        Builder setThreadsAwaitingConnection(Integer threadsAwaitingConnection) {
            this.threadsAwaitingConnection = threadsAwaitingConnection
            return this
        }

        ConnectionStatistic build() {
            return new ConnectionStatistic(totalConnections, activeConnections, idleConnections, threadsAwaitingConnection)
        }
    }
}
