/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { Create, Request } from "../../../../common/epics/EpicUtils";
import { getRecords } from "../../../../common/components/list-view/actions";
import { EntityName } from "../../../../model/entities/common";
import { updateEntityItemById, updateEntityItemByIdErrorHandler } from "../entityItemsService";
import { executeActionsQueue, FETCH_SUCCESS } from "../../../../common/actions";
import { getEntityRecord, UPDATE_ENTITY_RECORD_REQUEST } from "../actions";
import { mapEntityDisplayName } from "../utils";
import { State } from "../../../../reducers/state";
import { processNotesAsyncQueue } from "../../../../common/components/form/notes/utils";

export const getProcessDataActions = (entity: EntityName, state: State, id: number) => [
  executeActionsQueue(),
  {
    type: FETCH_SUCCESS,
    payload: { message: `${mapEntityDisplayName(entity)} updated` }
  },
  getRecords({ entity, listUpdate: true, savedID: id }),
  ...state.list.fullScreenEditView || state.list.records.layout === "Three column" ? [
    getEntityRecord(id, entity)
  ] : []
];

const request: Request<any, { item: any, entity: EntityName }> = {
  type: UPDATE_ENTITY_RECORD_REQUEST,
  getData: ({ item, entity }) => updateEntityItemById(entity, item.id, item),
  retrieveData: (p, s) => processNotesAsyncQueue(s.actionsQueue.queuedActions),
  processData: (v, s, { item, entity }) => getProcessDataActions(entity, s, item.id),
  processError: (response, { item, entity }) => updateEntityItemByIdErrorHandler(response, entity, item)
};

export const EpicUpdateEntityRecord: Epic = Create(request);