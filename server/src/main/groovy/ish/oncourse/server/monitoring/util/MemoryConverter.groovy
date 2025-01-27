/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.monitoring.util

import groovy.transform.CompileDynamic

import java.text.DecimalFormat


class MemoryConverter {

    private static final DecimalFormat df = new DecimalFormat("#.##");

    static String toPrettyMemorySize(long value) {

        if (value / 1024 / 1024 / 1024 > 1) {
            return df.format(value / 1024 / 1024 / 1024) + " " + MemoryUnit.GByte.displayName
        }
        if (value / 1024 / 1024 > 1) {
            return df.format(value / 1024 / 1024) + " " + MemoryUnit.MByte.displayName
        }
        if (value / 1024 > 1) {
            return df.format(value / 1024) + " " + MemoryUnit.KBytes.displayName
        }
        return value + MemoryUnit.Bytes.displayName
    }

    static Long toBytes(String size) throws NumberFormatException {
        MemoryUnit unit = MemoryUnit.fromValie(size.substring(size.size() - 2, size.size())) ?: MemoryUnit.Bytes
        Long memoryValue = Integer.parseInt(size.replaceFirst("(?i)${unit.displayName}", ""))

        switch (unit) {
            case MemoryUnit.GByte:
                return memoryValue * 1024 * 1024 * 1024
            case MemoryUnit.MByte:
                return memoryValue * 1024 * 1024
            case MemoryUnit.KBytes:
                return memoryValue * 1024
        }
        return memoryValue
    }
}

@CompileDynamic
enum MemoryUnit {

    Bytes ('bytes'),
    KBytes('Kb'),
    MByte('Mb'),
    GByte('Gb'),

    private displayName

    MemoryUnit(displayName) {
        this.displayName = displayName
    }

    String getDisplayName() {
        return displayName
    }

    static MemoryUnit fromValie(String unit) {
        return values().toList().find {it.displayName.equalsIgnoreCase(unit) }
    }
}
