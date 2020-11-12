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

package ish.payroll;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Artem on 15/02/2017.
 */
public class WagesSummaryResponse {

    private long unprocessedWagesCount;
    private long unconfirmedWagesCount;
    private long totalWagesCount;
    private Set<Long> unconfirmedClassesIds;

    public long getUnprocessedWagesCount() {
        return unprocessedWagesCount;
    }

    public void setUnprocessedWagesCount(long unprocessedWagesCount) {
        this.unprocessedWagesCount = unprocessedWagesCount;
    }

    public long getUnconfirmedWagesCount() {
        return unconfirmedWagesCount;
    }

    public void setUnconfirmedWagesCount(long unconfirmedWagesCount) {
        this.unconfirmedWagesCount = unconfirmedWagesCount;
    }

    public Set<Long> getUnconfirmedClassesIds() {
        return unconfirmedClassesIds;
    }

    public void setUnconfirmedClassesIds(Set<Long> unconfirmedClassesIds) {
        this.unconfirmedClassesIds = unconfirmedClassesIds;
    }

    public String getUnconfirmedClassesIdsString() {
        String result = unconfirmedClassesIds.stream()
                .map(n -> String.valueOf(n)).collect(Collectors.joining(", "));
        return result;
    }

    public long getTotalWagesCount() {
        return totalWagesCount;
    }

    public void setTotalWagesCount(long totalWagesCount) {
        this.totalWagesCount = totalWagesCount;
    }
}
