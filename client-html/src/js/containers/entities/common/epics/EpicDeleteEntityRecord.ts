/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { Create, Request } from "../../../../common/epics/EpicUtils";
import { EntityName } from "../../../../model/entities/common";
import { deleteEntityItemById, deleteEntityItemByIdErrorHandler, } from "../entityItemsService";
import { DELETE_ENTITY_RECORD_REQUEST } from "../actions";
import { getListRecordAfterDeleteActions } from "../utils";

const request: Request<any, { entity: EntityName, id: number }> = {
  type: DELETE_ENTITY_RECORD_REQUEST,
  getData: ({ entity, id }) => deleteEntityItemById(entity, id),
  processData: (v, s, { entity }) => getListRecordAfterDeleteActions(entity),
  processError: (response, { entity }) => deleteEntityItemByIdErrorHandler(response, entity)
};

export const EpicDeleteEntityRecord: Epic = Create(request);
