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
import {
  getEntityItemById,
  getEntityItemByIdErrorHandler
} from "../entityItemsService";
import { GET_ENTITY_RECORD_REQUEST } from "../actions";
import { getListRecordAfterGetActions } from "../utils";

const request: Request<any, { id: number, entity: EntityName }> = {
  type: GET_ENTITY_RECORD_REQUEST,
  getData: ({ id, entity }) => getEntityItemById(entity, id),
  processData: (item, s, { entity }) => getListRecordAfterGetActions(item, entity, s),
  processError: response => getEntityItemByIdErrorHandler(response)
};

export const EpicGetEntityRecord: Epic = Create(request);