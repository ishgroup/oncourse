/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { TableModel } from "@api/model";
import * as EpicUtils from "../../../epics/EpicUtils";
import EntityService from "../../../services/EntityService";
import FetchErrorHandler from "../../../api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST, UPDATE_TABLE_MODEL_REQUEST } from "../actions";

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
      filterColumnWidth: payload.model.filterColumnWidth || state.list.records.filterColumnWidth
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

    return EntityService.updateTableModel(payload.entity, updatedModel);
  },
  processData: (records: any, state: any, payload) => [
    ...(payload.listUpdate
      ? [
          {
            type: GET_RECORDS_REQUEST,
            payload: { entity: payload.entity, listUpdate: !payload.resetScroll }
          }
        ]
      : [])
  ],
  processError: response => FetchErrorHandler(response, "List settings was not saved")
};

export const EpicUpdateTableModel: Epic<any, any> = EpicUtils.Create(request);
