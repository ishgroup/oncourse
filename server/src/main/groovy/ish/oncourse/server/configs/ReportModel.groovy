/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.configs

import com.fasterxml.jackson.annotation.JsonInclude
import ish.oncourse.server.cayenne.Report


@JsonInclude(JsonInclude.Include.NON_NULL)
class ReportModel extends AutomationModel{
    private String entityClass
    private boolean isVisible
    private String sortOn

    ReportModel(Report report) {
        super(report)

        this.entityClass = report.entity
        this.isVisible = report.isVisible
        this.sortOn = report.sortOn
    }


    String getEntityClass() {
        return entityClass
    }

    boolean getIsVisible() {
        return isVisible
    }

    String getSortOn() {
        return sortOn
    }
}
