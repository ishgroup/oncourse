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
import { createEntityItem, createEntityItemByIdErrorHandler } from "../entityItemsService";
import { executeActionsQueue, FETCH_SUCCESS } from "../../../../common/actions";
import { CREATE_ENTITY_RECORD_REQUEST } from "../actions";
import { mapEntityDisplayName } from "../utils";
import { processNotesAsyncQueue } from "../../../../common/components/form/notes/utils";

export const getProcessDataActions = (entity: EntityName) => [
  executeActionsQueue(),
  {
    type: FETCH_SUCCESS,
    payload: { message: `${mapEntityDisplayName(entity)} created` }
  },
  getRecords({ entity, listUpdate: true }),
  setListSelection([]),
  initialize(LIST_EDIT_VIEW_FORM_NAME, null)
];

const request: Request<any, { item: any, entity: EntityName }> = {
  type: CREATE_ENTITY_RECORD_REQUEST,
  getData: ({ item, entity }) => createEntityItem(entity, item),
  retrieveData: (p, s) => processNotesAsyncQueue(s.actionsQueue.queuedActions),
  processData: (v, s, { entity }) => getProcessDataActions(entity),
  processError: (response, { item, entity }) => createEntityItemByIdErrorHandler(response, entity, item)
};

export const EpicCreateEntityRecord: Epic = Create(request);