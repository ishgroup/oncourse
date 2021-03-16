/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";
import * as EpicUtils from "../../../epics/EpicUtils";
import { State } from "../../../../reducers/state";
import { GET_LIST_NESTED_EDIT_RECORD, SET_LIST_NESTED_EDIT_RECORD } from "../actions/index";
import { getEntityItemById } from "../../../../containers/entities/common/entityItemsService";

const request: EpicUtils.Request<any, { entity: string; id: number; index?: number; threeColumn?: boolean }> = {
  type: GET_LIST_NESTED_EDIT_RECORD,
  getData: ({ entity, id, threeColumn }, state) => {
    if (threeColumn && entity === state.list.records.entity) {
      return new Promise(resolve => resolve(state.list.editRecord));
    }
    return getEntityItemById(entity, id);
  },
  processData: (record: any, state: State, { index, entity }) => (typeof index === "number"
      ? [initialize(`NestedEditViewForm[${index}]`, record)]
      : [
          {
            type: SET_LIST_NESTED_EDIT_RECORD,
            payload: {
              entity,
              record,
              opened: true
            }
          },
          initialize(`NestedEditViewForm[${state.list.nestedEditRecords.length || 0}]`, record)
        ])
};

export const EpicGetListNestedEditRecord: Epic<any, any> = EpicUtils.Create(request);
