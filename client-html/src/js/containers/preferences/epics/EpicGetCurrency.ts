/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../common/epics/EpicUtils";
import { GET_CURRENCY, GET_CURRENCY_FULFILLED } from "../actions";
import { Currency } from "@api/model";
import PreferencesService from "../services/PreferencesService";

const request: EpicUtils.Request = {
  type: GET_CURRENCY,
  getData: () => {
    return PreferencesService.getCurrency();
  },
  processData: (currency: Currency) => {
    return [
      {
        type: GET_CURRENCY_FULFILLED,
        payload: { currency }
      }
    ];
  }
};

export const EpicGetCurrency: Epic<any, any> = EpicUtils.Create(request);
