/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { SessionStatus } from '@api/model';
import { Epic } from 'redux-observable';
import FetchErrorHandler from '../../../../common/api/fetch-errors-handlers/FetchErrorHandler';
import * as EpicUtils from '../../../../common/epics/EpicUtils';
import {
  CHECKOUT_GET_PAYMENT_STATUS_DETAILS,
  checkoutGetPaymentStatusDetails,
  checkoutPaymentSetCustomStatus,
  checkoutSetPaymentDetailsFetching,
  checkoutSetPaymentStatusDetails,
  checkoutSetPaymentSuccess
} from '../../actions/checkoutPayment';
import CheckoutService from '../../services/CheckoutService';

const request: EpicUtils.DelayedRequest<SessionStatus, { sessionId: string, withTimeout?: boolean }> = {
  type: CHECKOUT_GET_PAYMENT_STATUS_DETAILS,
  delay: ({ withTimeout }) => withTimeout ? 1000 : 0,
  getData: ({ sessionId }) => CheckoutService.getSessionStatus(sessionId),
  processData: (data, s, { sessionId }) => {

    const actions: any = [
      checkoutSetPaymentStatusDetails(data),
      checkoutSetPaymentDetailsFetching(false)
    ];

    if (data.complete) {
      actions.push(
        checkoutSetPaymentSuccess(true),
        checkoutPaymentSetCustomStatus("success")
      );
    } else if (data.authorised) {
      return [checkoutGetPaymentStatusDetails(sessionId, true)];
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

export const EpicCheckoutGetPaymentStatusDetails: Epic<any, any> = EpicUtils.CreateWithTimeout(request);