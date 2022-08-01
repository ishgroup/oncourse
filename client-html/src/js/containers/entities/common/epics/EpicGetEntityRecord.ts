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
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { EntityName } from "../../../../model/entities/common";
import {
  getEntityItemById,
  getEntityItemByIdErrorHandler
} from "../entityItemsService";
import { GET_ENTITY_RECORD_REQUEST } from "../actions";
import { mapEntityListDisplayName } from "../utils";
import { getNoteItems } from "../../../../common/components/form/notes/actions";
import { clearActionsQueue } from "../../../../common/actions";
import { State } from "../../../../reducers/state";
import { NOTE_ENTITIES } from "../../../../constants/Config";

export const getProcessDataActions = (item: any, entity: EntityName, state: State) => [
  {
    type: SET_LIST_EDIT_RECORD,
    payload: { editRecord: item, name: mapEntityListDisplayName(entity, item, state) }
  },
  ...NOTE_ENTITIES.includes(entity) ? [getNoteItems(entity, item.id, LIST_EDIT_VIEW_FORM_NAME)] : [],
  initialize(LIST_EDIT_VIEW_FORM_NAME, item),
  ...(state.actionsQueue.queuedActions.length ? [clearActionsQueue()] : [])
];

const request: Request<any, { id: number, entity: EntityName }> = {
  type: GET_ENTITY_RECORD_REQUEST,
  getData: ({ id, entity }) => getEntityItemById(entity, id),
  processData: (item, s, { entity }) => getProcessDataActions(item, entity, s),
  processError: response => getEntityItemByIdErrorHandler(response)
};

export const EpicGetEntityRecord: Epic = Create(request);