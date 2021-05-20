/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { Epic } from "redux-observable";
import PreferencesService from "../../../preferences/services/PreferencesService";
import {
  CHECKOUT_GET_ACTIVE_PAYMENT_TYPES,
  CHECKOUT_GET_ACTIVE_PAYMENT_TYPES_FULFILLED
} from "../../actions/checkoutPayment";

const request: EpicUtils.Request = {
  type: CHECKOUT_GET_ACTIVE_PAYMENT_TYPES,
  getData: () => PreferencesService.getPaymentTypes(),
  processData: (paymentTypes: any) => {
    const activePaymentTypes = paymentTypes.filter(({ active, systemType }) => active && !systemType);
    return [
      {
        type: CHECKOUT_GET_ACTIVE_PAYMENT_TYPES_FULFILLED,
        payload: { paymentTypes: activePaymentTypes }
      }
    ];
  }
};

export const EpicCheckoutGetActivePaymentTypes: Epic<any, any> = EpicUtils.Create(request);
