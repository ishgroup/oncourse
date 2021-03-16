/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import { GET_ACCOUNT_TRANSACTION_LOCKED_DATE, GET_ACCOUNT_TRANSACTION_LOCKED_DATE_FULFILLED } from "../actions";
import PreferencesService from "../services/PreferencesService";

const request: EpicUtils.Request = {
  type: GET_ACCOUNT_TRANSACTION_LOCKED_DATE,
  getData: () => {
    return PreferencesService.getLockedDate();
  },
  processData: (lockedDate: Date) => {
    return [
      {
        type: GET_ACCOUNT_TRANSACTION_LOCKED_DATE_FULFILLED,
        payload: { lockedDate }
      }
    ];
  }
};

export const EpicGetLockedDate: Epic<any, any> = EpicUtils.Create(request);
