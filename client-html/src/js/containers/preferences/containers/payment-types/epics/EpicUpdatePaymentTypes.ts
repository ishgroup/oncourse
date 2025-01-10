/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { PaymentType } from "@api/model";
import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { UPDATE_PAYMENT_TYPES_FULFILLED, UPDATE_PAYMENT_TYPES_REQUEST } from "../../../actions";
import PreferencesService from "../../../services/PreferencesService";

const request: EpicUtils.Request = {
  type: UPDATE_PAYMENT_TYPES_REQUEST,
  getData: payload => PreferencesService.updatePaymentTypes(payload.paymentTypes),
  retrieveData: () => PreferencesService.getPaymentTypes(),
  processData: (items: PaymentType[]) => {
    return [
      {
        type: UPDATE_PAYMENT_TYPES_FULFILLED,
        payload: { paymentTypes: items }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Payment Types were successfully updated" }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response, "Error. Payment Types was not updated");
  }
};

export const EpicUpdatePaymentTypes: Epic<any, any> = EpicUtils.Create(request);
