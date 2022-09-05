/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { Create, Request } from "../../../../common/epics/EpicUtils";
import { getRecords, setListSelection } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { EntityName } from "../../../../model/entities/common";
import { deleteEntityItemById, deleteEntityItemByIdErrorHandler, } from "../entityItemsService";
import { FETCH_SUCCESS } from "../../../../common/actions";
import { DELETE_ENTITY_RECORD_REQUEST } from "../actions";
import { mapEntityDisplayName } from "../utils";

export const getProcessDataActions = (entity: EntityName) => [
  {
    type: FETCH_SUCCESS,
    payload: { message: `${mapEntityDisplayName(entity)} deleted` }
  },
  getRecords({ entity, listUpdate: true }),
  setListSelection([]),
  initialize(LIST_EDIT_VIEW_FORM_NAME, null)
];

const request: Request<any, { entity: EntityName, id: number }> = {
  type: DELETE_ENTITY_RECORD_REQUEST,
  getData: ({ entity, id }) => deleteEntityItemById(entity, id),
  processData: (v, s, { entity }) => getProcessDataActions(entity),
  processError: (response, { entity }) => deleteEntityItemByIdErrorHandler(response, entity)
};

export const EpicDeleteEntityRecord: Epic = Create(request);
