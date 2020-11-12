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
  GET_COMMON_PLAIN_RECORDS_ACTIONS_FULFILLED,
  GET_COMMON_PLAIN_RECORDS_FULFILLED
} from "../actions/CommonPlainRecordsActions";
import * as EpicUtils from "./EpicUtils";

const request: EpicUtils.Request<
  DataResponse,
  State,
  { key?: string; offset?: number; columns?: string; ascending?: boolean; sort?: string }
> = {
  type: GET_COMMON_PLAIN_RECORDS,
  hideLoadIndicator: true,
  getData: ({
 key, offset, columns, ascending, sort
}, { plainSearchRecords }) => {
    const plainSearchRecord = plainSearchRecords[key];
    return EntityService.getPlainRecords(
      plainSearchRecord.entity,
      columns,
      plainSearchRecord.search,
      100,
      offset,
      sort,
      ascending
    );
  },
  processData: (records, { plainSearchRecords }, { key, columns }) => {
    const { rows, offset, pageSize } = records;
    const items = rows.map(getCustomColumnsMap(columns));

    return [
      {
        type: GET_COMMON_PLAIN_RECORDS_FULFILLED,
        payload: {
         key, items, offset, pageSize
        }
      },
      plainSearchRecords[key].actions
        ? plainSearchRecords[key].actions(items, offset, pageSize)
        : { type: GET_COMMON_PLAIN_RECORDS_ACTIONS_FULFILLED }
    ];
  }
};

export const EpicGetCommonPlainRecords: Epic<any, any> = EpicUtils.Create(request);
