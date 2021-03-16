/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../epics/EpicUtils";
import EntityService from "../../../services/EntityService";
import { GET_PLAIN_RECORDS_REQUEST, GET_PLAIN_RECORDS_REQUEST_FULFILLED } from "../actions";
import { DataResponse } from "@api/model";

const request: EpicUtils.Request = {
  type: GET_PLAIN_RECORDS_REQUEST,
  getData: payload => {
    return EntityService.getPlainRecords(payload.entity, payload.columns, payload.search);
  },
  processData: (plainRecords: DataResponse) => {
    return [
      {
        type: GET_PLAIN_RECORDS_REQUEST_FULFILLED,
        payload: { plainRecords }
      }
    ];
  }
};

export const EpicGetPlainEntities: Epic<any, any> = EpicUtils.Create(request);
