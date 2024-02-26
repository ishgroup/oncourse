/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.monitoring.model.datasource

class HikariConfig {

    private Long connectionTimeout
    private Long validationTimeout
    private Long idleTimeout
    private Long maxLifetime
    private Integer minimumIdle
    private Integer maximumPoolSize
    private Long leakDetectionThreshold

    HikariConfig(Long connectionTimeout, Long validationTimeout, Long idleTimeout, Long maxLifetime, Integer minimumIdle, Integer maximumPoolSize, Long leakDetectionThreshold) {
        this.connectionTimeout = connectionTimeout
        this.validationTimeout = validationTimeout
        this.idleTimeout = idleTimeout
        this.maxLifetime = maxLifetime
        this.minimumIdle = minimumIdle
        this.maximumPoolSize = maximumPoolSize
        this.leakDetectionThreshold = leakDetectionThreshold
    }

    Long getConnectionTimeout() {
        return connectionTimeout
    }

    Long getValidationTimeout() {
        return validationTimeout
    }

    Long getIdleTimeout() {
        return idleTimeout
    }

    Long getMaxLifetime() {
        return maxLifetime
    }

    Integer getMinimumIdle() {
        return minimumIdle
    }

    Integer getMaximumPoolSize() {
        return maximumPoolSize
    }

    Long getLeakDetectionThreshold() {
        return leakDetectionThreshold
    }

    static class Builder {

        private Long connectionTimeout = null
        private Long validationTimeout = null
        private Long idleTimeout = null
        private Long maxLifetime = null
        private Integer minimumIdle = null
        private Integer maximumPoolSize = null
        private Long leakDetectionThreshold = null

        Builder setConnectionTimeout(Long connectionTimeout) {
            this.connectionTimeout = connectionTimeout
            return this
        }

        Builder setValidationTimeout(Long validationTimeout) {
            this.validationTimeout = validationTimeout
            return this
        }

        Builder setIdleTimeout(Long idleTimeout) {
            this.idleTimeout = idleTimeout
            return this
        }

        Builder setMaxLifetime(Long maxLifetime) {
            this.maxLifetime = maxLifetime
            return this
        }

        Builder setMinimumIdle(Integer minimumIdle) {
            this.minimumIdle = minimumIdle
            return this
        }

        Builder setMaximumPoolSize(Integer maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize
            return this
        }

        Builder setLeakDetectionThreshold(Long leakDetectionThreshold) {
            this.leakDetectionThreshold = leakDetectionThreshold
            return this
        }

        HikariConfig build() {
            return new HikariConfig(connectionTimeout, validationTimeout, idleTimeout, maxLifetime, minimumIdle, maximumPoolSize, leakDetectionThreshold)
        }
    }
}
