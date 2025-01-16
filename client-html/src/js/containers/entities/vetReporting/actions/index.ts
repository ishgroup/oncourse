/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { _toRequestType } from "../../../../common/actions/ActionUtils";
import { ListActionEntity } from "../../../../model/entities/common";
import { VetReport } from "../../../../model/entities/VetReporting";

export const UPDATE_VET_REPORT_ENTITIES = _toRequestType("update/vetReport/entities");

export const updateVetReportEntities = (entity: ListActionEntity, item: VetReport) => ({
  type: UPDATE_VET_REPORT_ENTITIES,
  payload: { entity, item }
});