/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { PaymentType } from "@api/model";
import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { DELETE_PAYMENT_TYPE_FULFILLED, DELETE_PAYMENT_TYPE_REQUEST } from "../../../actions";
import PreferencesService from "../../../services/PreferencesService";

const request: EpicUtils.Request = {
  type: DELETE_PAYMENT_TYPE_REQUEST,
  getData: payload => PreferencesService.deletePaymentType(payload.id),
  retrieveData: () => PreferencesService.getPaymentTypes(),
  processData: (items: PaymentType[]) => {
    return [
      {
        type: DELETE_PAYMENT_TYPE_FULFILLED,
        payload: { paymentTypes: items }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Payment Type was successfully deleted" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Payment Type was not deleted");
  }
};

export const EpicDeletePaymentType: Epic<any, any> = EpicUtils.Create(request);
