/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.monitoring.model.memory

class MemoryStatistic {

    private Long maxHeapSize
    private Long currentHeapSize
    private Long usedHeapMemory
    private Long usedNotHeapMemory

    MemoryStatistic(Long maxHeapSize, Long currentHeapSize, Long usedHeapMemory, Long usedNotHeapMemory) {
        this.maxHeapSize = maxHeapSize
        this.currentHeapSize = currentHeapSize
        this.usedHeapMemory = usedHeapMemory
        this.usedNotHeapMemory = usedNotHeapMemory
    }

    Long getMaxHeapSize() {
        return maxHeapSize
    }

    Long getCurrentHeapSize() {
        return currentHeapSize
    }

    Long getUsedHeapMemory() {
        return usedHeapMemory
    }

    Long getUsedNotHeapMemory() {
        return usedNotHeapMemory
    }

    static class Builder {

        private Long maxHeapSize = null
        private Long currentHeapSize = null
        private Long usedHeapMemory = null
        private Long notHeapMemory = null

        Builder setMaxHeapSize(Long maxHeapSize) {
            this.maxHeapSize = maxHeapSize
            return this
        }

        Builder setCurrentHeapSize(Long currentHeapSize) {
            this.currentHeapSize = currentHeapSize
            return this
        }

        Builder setUsedHeapMemory(Long usedMemory) {
            this.usedHeapMemory = usedMemory
            return this
        }

        Builder setNotHeapMemory(Long notHeapMemory) {
            this.notHeapMemory = notHeapMemory
            return this
        }

        MemoryStatistic build() {
            return new MemoryStatistic(maxHeapSize, currentHeapSize, usedHeapMemory, notHeapMemory)
        }
    }
}
