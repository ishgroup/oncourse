/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { DataResponse } from "@api/model";
import { State } from "../../reducers/state";
import EntityService from "../services/EntityService";
import { getCustomColumnsMap } from "../utils/common";
import {
  GET_COMMON_PLAIN_RECORDS,
  GET_COMMON_PLAIN_RECORDS_FULFILLED
} from "../actions/CommonPlainRecordsActions";
import * as EpicUtils from "./EpicUtils";

const request: EpicUtils.Request<
  DataResponse,
  State,
  { key?: string; offset?: number; columns?: string; ascending?: boolean; sort?: string, pageSize?: number }
> = {
  type: GET_COMMON_PLAIN_RECORDS,
  hideLoadIndicator: true,
  getData: ({
   key, offset, columns, ascending, sort, pageSize
  }, { plainSearchRecords }) => EntityService.getPlainRecords(
    key,
    columns,
    plainSearchRecords[key].search,
    pageSize || 100,
    offset,
    sort,
    ascending
  ),
  processData: (records, s, { key, columns }) => {
    const { rows, offset, pageSize } = records;
    const items = rows.map(getCustomColumnsMap(columns));

    return [
      {
        type: GET_COMMON_PLAIN_RECORDS_FULFILLED,
        payload: {
         key, items, offset, pageSize
        }
      }
    ];
  }
};

export const EpicGetCommonPlainRecords: Epic<any, any> = EpicUtils.Create(request);
