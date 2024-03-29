/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { TableModel } from "@api/model";
import { Epic } from "redux-observable";
import FetchErrorHandler from "../../../api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../epics/EpicUtils";
import EntityService from "../../../services/EntityService";
import { GET_RECORDS_REQUEST, UPDATE_TABLE_MODEL_REQUEST, UPDATE_TAGS_ORDER } from "../actions";

const request: EpicUtils.Request<
  any,
  { entity: string; model: TableModel; listUpdate?: boolean; resetScroll?: boolean }
> = {
  type: UPDATE_TABLE_MODEL_REQUEST,
  getData: (payload, state) => {
    const updatedModel: TableModel = {
      sortings: payload.model.sortings || state.list.records.sort,
      columns: payload.model.columns || state.list.records.columns || [],
      layout: payload.model.layout || state.list.records.layout,
      filterColumnWidth: payload.model.filterColumnWidth || state.list.records.filterColumnWidth,
      tagsOrder: payload.model.tagsOrder || state.list.records.tagsOrder,
    };
    updatedModel.sortings.forEach(sorting => {
      updatedModel.columns.forEach(column => {
        if (sorting.attribute === column.attribute) {
          sorting.complexAttribute = column.sortFields;
        }
      });
    });

    if (!updatedModel.columns.length) {
      throw Error("Table columns are empty!");
    }

    return EntityService.updateTableModel(state.list.customTableModel || payload.entity, updatedModel);
  },
  processData: (records: any, state: any, payload) => [
    ...(payload.listUpdate
      ? [
        {
          type: GET_RECORDS_REQUEST,
          payload: {entity: payload.entity, listUpdate: !payload.resetScroll}
        },
        {
          type: UPDATE_TAGS_ORDER,
          payload: payload.model.tagsOrder || state.list.records.tagsOrder,
        }
      ]
      : [])
  ],
  processError: response => FetchErrorHandler(response, "List settings was not saved")
};

export const EpicUpdateTableModel: Epic<any, any> = EpicUtils.Create(request);