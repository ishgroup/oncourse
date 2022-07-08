/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { DataResponse } from "@api/model";
import EntityService from "../services/EntityService";
import { getCustomColumnsMap } from "../utils/common";
import {
  GET_COMMON_PLAIN_RECORDS,
  GET_COMMON_PLAIN_RECORDS_FULFILLED,
  getCommonPlainRecordsRejected
} from "../actions/CommonPlainRecordsActions";
import * as EpicUtils from "./EpicUtils";

const request: EpicUtils.Request<
  DataResponse,
  { key?: string; offset?: number; columns?: string; ascending?: boolean; sort?: string, pageSize?: number, customColumnMap?: any }
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
  processData: (records, s, { key, columns, customColumnMap }) => {
    const { rows, offset, pageSize } = records;
    let items = rows.map(getCustomColumnsMap(columns));

    if (typeof customColumnMap === "function") {
      items = items.map(customColumnMap);
    }

    return [
      {
        type: GET_COMMON_PLAIN_RECORDS_FULFILLED,
        payload: {
          key, items, offset, pageSize
        }
      }
    ];
  },
  processError: (e, { key }) => [getCommonPlainRecordsRejected(key)]
};

export const EpicGetCommonPlainRecords: Epic<any, any> = EpicUtils.Create(request);
