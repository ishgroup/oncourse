/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { GET_PAYMENT_TYPES_REQUEST, GET_PAYMENT_TYPES_FULFILLED } from "../../../actions";

const request: EpicUtils.Request<any, any, any> = {
  type: GET_PAYMENT_TYPES_REQUEST,
  getData: () => PreferencesService.getPaymentTypes(),
  processData: paymentTypes => {
    return [
      {
        type: GET_PAYMENT_TYPES_FULFILLED,
        payload: { paymentTypes }
      }
    ];
  }
};

export const EpicGetPaymentTypes: Epic<any, any> = EpicUtils.Create(request);
