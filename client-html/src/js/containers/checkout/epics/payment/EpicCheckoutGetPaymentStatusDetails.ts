/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import FetchErrorHandler from '../../../../common/api/fetch-errors-handlers/FetchErrorHandler';
import * as EpicUtils from '../../../../common/epics/EpicUtils';
import {
  CHECKOUT_GET_PAYMENT_STATUS_DETAILS,
  checkoutPaymentSetCustomStatus,
  checkoutSetPaymentDetailsFetching,
  checkoutSetPaymentStatusDetails,
  checkoutSetPaymentSuccess
} from '../../actions/checkoutPayment';
import CheckoutService from '../../services/CheckoutService';

const request: EpicUtils.Request<any, { status: any; sessionId: string }> = {
  type: CHECKOUT_GET_PAYMENT_STATUS_DETAILS,
  getData: ({ sessionId }) => CheckoutService.getSessionStatus(sessionId),
  processData: (data, { checkout: { payment: { merchantReference } } }, { sessionId }) => {

    const actions: any = [
      checkoutSetPaymentStatusDetails(data),
      checkoutSetPaymentDetailsFetching(false)
    ];

    if (data.complete) {
      actions.push(
        checkoutSetPaymentSuccess(true),
        checkoutPaymentSetCustomStatus("success")
      );
    } else {
      actions.push(
        checkoutSetPaymentSuccess(false),
        checkoutPaymentSetCustomStatus("fail")
      );
    }

    return actions;
  },
  processError: err => [
    checkoutSetPaymentDetailsFetching(false),
    ...FetchErrorHandler(err, "Failed to get payment details")
  ]
};

export const EpicCheckoutGetPaymentStatusDetails: Epic<any, any> = EpicUtils.Create(request);