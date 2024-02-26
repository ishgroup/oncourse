/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.monitoring.model.system

class SystemStatistic {

    private Double averageCpuLoad
    private Integer numberOfCpu
    private Integer activeThreadsCount

    SystemStatistic(Double averageCpuLoad, Integer numberOfCpu, Integer activeThreadsCount) {
        this.averageCpuLoad = averageCpuLoad
        this.numberOfCpu = numberOfCpu
        this.activeThreadsCount = activeThreadsCount
    }

    Double getAverageCpuLoad() {
        return averageCpuLoad
    }

    void setAverageCpuLoad(Double averageCpuLoad) {
        this.averageCpuLoad = averageCpuLoad
    }

    Integer getNumberOfCpu() {
        return numberOfCpu
    }

    void setNumberOfCpu(Integer numberOfCpu) {
        this.numberOfCpu = numberOfCpu
    }

    Integer getActiveThreadsCount() {
        return activeThreadsCount
    }

    void setActiveThreadsCount(Integer activeThreadsCount) {
        this.activeThreadsCount = activeThreadsCount
    }

    static class Builder {

        private Double averageCpuLoad = null
        private Integer numberOfCpu = null
        private Integer activeThreadsCount = null

        Builder setAverageCpuLoad(Double averageCpuLoad) {
            this.averageCpuLoad = averageCpuLoad
            return this
        }

        Builder setNumberOfCpu(Integer numberOfCpu) {
            this.numberOfCpu = numberOfCpu
            return this
        }

        Builder setActiveThreadsCount(Integer activeThreadsCount) {
            this.activeThreadsCount = activeThreadsCount
            return this
        }

        SystemStatistic build() {
            return new SystemStatistic(averageCpuLoad, numberOfCpu, activeThreadsCount)
        }
    }
}
