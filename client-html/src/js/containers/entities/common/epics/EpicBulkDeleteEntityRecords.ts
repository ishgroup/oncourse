/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Diff } from "@api/model";
import { Epic } from "redux-observable";
import { Create, Request } from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { ListActionEntity } from "../../../../model/entities/common";
import { BULK_DELETE_ENTITY_RECORDS_REQUEST } from "../actions";
import { getListRecordAfterBulkDeleteActions } from "../utils";

const request: Request<any, { entity: ListActionEntity, diff: Diff }> = {
  type: BULK_DELETE_ENTITY_RECORDS_REQUEST,
  getData: ({ entity, diff }) => EntityService.bulkDelete(entity, diff),
  processData: (v, s, { entity }) => getListRecordAfterBulkDeleteActions(entity),
};

export const EpicBulkDeleteEntityRecords: Epic = Create(request);