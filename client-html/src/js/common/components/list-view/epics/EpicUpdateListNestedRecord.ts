/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../epics/EpicUtils";
import { State } from "../../../../reducers/state";
import { GET_LIST_NESTED_EDIT_RECORD, GET_RECORDS_REQUEST, UPDATE_LIST_NESTED_EDIT_RECORD } from "../actions";
import { FETCH_SUCCESS } from "../../../actions";
import {
  updateEntityItemById,
  updateEntityItemByIdErrorHandler
} from "../../../../containers/entities/common/entityItemsService";
import { EntityName } from "../../../../model/entities/common";
import { NESTED_EDIT_VIEW_FORM_NAME } from "../../../../constants/Forms";

const request: EpicUtils.Request<
  any,
  { entity: EntityName; id: number; record: any; index: number; listRootEntity: string; }
> = {
  type: UPDATE_LIST_NESTED_EDIT_RECORD,
  getData: payload => updateEntityItemById(payload.entity, payload.id, payload.record),
  processData: (record: any, state: State, {
 entity, id, index, listRootEntity 
}) => [
      {
        type: FETCH_SUCCESS,
        payload: { message: `${entity} was updated` }
      },
      {
        type: GET_LIST_NESTED_EDIT_RECORD,
        payload: { entity, id, index }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: {
          entity: listRootEntity,
          listUpdate: true,
          savedID: state.list.editRecord ? state.list.editRecord.id : id
        }
      }
    ],
  processError: (response, { entity, index, record }) =>
    updateEntityItemByIdErrorHandler(response, entity, `${NESTED_EDIT_VIEW_FORM_NAME}[${index}]`, record)
};

export const EpicUpdateListNestedRecord: Epic<any, any> = EpicUtils.Create(request);
