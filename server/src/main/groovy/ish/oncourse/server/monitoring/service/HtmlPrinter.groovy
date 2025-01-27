/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.monitoring.service

import ish.oncourse.server.monitoring.model.MonitoringStatictic
import ish.oncourse.server.monitoring.util.ListItem
import ish.oncourse.server.monitoring.util.MonitoringPrinter

import static ish.oncourse.server.monitoring.util.MemoryConverter.*

class HtmlPrinter implements Printer {

    @Override
    String print(MonitoringStatictic monitoringStatictic) {
        StringBuilder output = new StringBuilder()

        if (monitoringStatictic.connectionStatistic) {

            List<ListItem> listItems = new ArrayList<>()
            output.append("<p> Database Pool size statictic: </p>")

            if (monitoringStatictic.connectionStatistic.totalConnections != null) {
                listItems.add(new ListItem('totalConnections', 'Total connections',
                        monitoringStatictic.connectionStatistic.totalConnections.toString())
                )
            }
            if (monitoringStatictic.connectionStatistic.idleConnections != null) {
                listItems.add(new ListItem('idleConnections', 'Idle Connections',
                        monitoringStatictic.connectionStatistic.idleConnections.toString())
                )
            }
            if (monitoringStatictic.connectionStatistic.activeConnections != null) {
                listItems.add(new ListItem('activeConnections', 'Active connections (in use)',
                        monitoringStatictic.connectionStatistic.activeConnections.toString())
                )
            }
            if (monitoringStatictic.connectionStatistic.threadsAwaitingConnection != null) {
                listItems.add(new ListItem('threadsAwaitingConnection', 'The count of threads waiting for a connection',
                        monitoringStatictic.connectionStatistic.threadsAwaitingConnection.toString())
                )
            }
            output.append(MonitoringPrinter.printList(listItems))
        }

        if (monitoringStatictic.memoryStatistic) {

            List<ListItem> listItems = new ArrayList<>()
            output.append("<p> Memory statictic: </p>")

            if (monitoringStatictic.memoryStatistic.maxHeapSize != null) {
                listItems.add(new ListItem('maxMemory', 'Max heap size',
                        toPrettyMemorySize(monitoringStatictic.memoryStatistic.maxHeapSize))
                )
            }
            if (monitoringStatictic.memoryStatistic.currentHeapSize != null) {
                listItems.add(new ListItem('currentHeapSize', 'Current heap size',
                        toPrettyMemorySize(monitoringStatictic.memoryStatistic.currentHeapSize))
                )
            }
            if (monitoringStatictic.memoryStatistic.usedHeapMemory != null) {
                listItems.add(new ListItem('usedHeapMemory', 'Used heap memory space',
                        toPrettyMemorySize(monitoringStatictic.memoryStatistic.usedHeapMemory))
                )
            }
            if (monitoringStatictic.memoryStatistic.usedNotHeapMemory != null) {
                listItems.add(new ListItem('usedNotHeapMemory', 'Used not heap memory space',
                        toPrettyMemorySize(monitoringStatictic.memoryStatistic.usedNotHeapMemory))
                )
            }
            if (monitoringStatictic.memoryStatistic.usedHeapMemory != null && monitoringStatictic.memoryStatistic.usedNotHeapMemory != null) {
                listItems.add(new ListItem('usedMemory', 'Total used memory',
                        toPrettyMemorySize(monitoringStatictic.memoryStatistic.usedHeapMemory + monitoringStatictic.memoryStatistic.usedNotHeapMemory))
                )
            }
            output.append(MonitoringPrinter.printList(listItems))
        }

        if (monitoringStatictic.hikariConfig) {

            List<ListItem> listItems = new ArrayList<>()
            output.append("<p> Hikari config: </p>")

            if (monitoringStatictic.hikariConfig.connectionTimeout != null) {
                listItems.add(new ListItem('connectionTimeout', 'connectionTimeout', monitoringStatictic.hikariConfig.connectionTimeout + ' ms'))
            }
            if (monitoringStatictic.hikariConfig.validationTimeout != null) {
                listItems.add(new ListItem('validationTimeout', 'validationTimeout', monitoringStatictic.hikariConfig.validationTimeout + ' ms'))
            }
            if (monitoringStatictic.hikariConfig.idleTimeout != null) {
                listItems.add(new ListItem('idleTimeout', 'idleTimeout', monitoringStatictic.hikariConfig.idleTimeout + ' ms'))

            }
            if (monitoringStatictic.hikariConfig.maxLifetime != null) {
                listItems.add(new ListItem('maxLifetime', 'maxLifetime', monitoringStatictic.hikariConfig.maxLifetime + ' ms'))
            }
            if (monitoringStatictic.hikariConfig.minimumIdle != null) {
                listItems.add(new ListItem('minimumIdle', 'minimumIdle', monitoringStatictic.hikariConfig.minimumIdle.toString())
                )
            }
            if (monitoringStatictic.hikariConfig.maximumPoolSize != null) {
                listItems.add(new ListItem('maximumPoolSize', 'maximumPoolSize', monitoringStatictic.hikariConfig.maximumPoolSize.toString()))
            }
            if (monitoringStatictic.hikariConfig.leakDetectionThreshold != null) {
                listItems.add(new ListItem('leakDetectionThreshold', 'leakDetectionThreshold', monitoringStatictic.hikariConfig.leakDetectionThreshold + ' ms'))
            }
            output.append(MonitoringPrinter.printList(listItems))
        }

        if (monitoringStatictic.systemStatistic) {

            List<ListItem> listItems = new ArrayList<>()
            output.append("<p> System statistic: </p>")

            if (monitoringStatictic.systemStatistic.averageCpuLoad != null) {
                listItems.add(new ListItem('averageCpuLoad', 'Average load of CPUs', monitoringStatictic.systemStatistic.averageCpuLoad + ' %'))
            }

            if (monitoringStatictic.systemStatistic.numberOfCpu != null) {
                listItems.add(new ListItem('numbOfCpu', 'Number of CPU', monitoringStatictic.systemStatistic.numberOfCpu +''))
            }

            if (monitoringStatictic.systemStatistic.activeThreadsCount != null) {
                listItems.add(new ListItem('activeThreads', 'Active threads number', monitoringStatictic.systemStatistic.activeThreadsCount + ''))
            }

            output.append(MonitoringPrinter.printList(listItems))
        }

        if (monitoringStatictic.environmentProperties) {

            List<ListItem> listItems = new ArrayList<>()
            output.append("<p> Environment properties: </p>")

            monitoringStatictic.environmentProperties.forEach { key, value ->
                listItems.add(new ListItem(key as String, key as String, (value as String) + ''))
            }

            output.append(MonitoringPrinter.printList(listItems))
        }

        if (monitoringStatictic.networkProperties) {

            List<ListItem> listItems = new ArrayList<>()
            output.append("<p> Network properties: </p>")

            monitoringStatictic.networkProperties.forEach { key, value ->
                listItems.add(new ListItem(key as String, key as String, (value as String) + ''))
            }

            output.append(MonitoringPrinter.printList(listItems))
        }

        if (monitoringStatictic.licenseProperties) {

            List<ListItem> listItems = new ArrayList<>()
            output.append("<p> License properties: </p>")

            monitoringStatictic.licenseProperties.forEach { key, value ->
                listItems.add(new ListItem(key as String, key as String, (value as String) + ''))
            }

            output.append(MonitoringPrinter.printList(listItems))
        }

        if (monitoringStatictic.onCourseProperties) {

            List<ListItem> listItems = new ArrayList<>()
            output.append("<p> onCourse properties: </p>")

            monitoringStatictic.onCourseProperties.forEach { key, value ->
                listItems.add(new ListItem(key as String, key as String, (value as String) + ''))
            }

            output.append(MonitoringPrinter.printList(listItems))
        }

        return output.toString()
    }
}
