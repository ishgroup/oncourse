/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { CheckoutCCResponse } from '@api/model';
import { Epic } from 'redux-observable';
import { SHOW_MESSAGE } from '../../../../common/actions';
import FetchErrorHandler from '../../../../common/api/fetch-errors-handlers/FetchErrorHandler';
import * as EpicUtils from '../../../../common/epics/EpicUtils';
import {
  CHECKOUT_COMPLETE_WINDCAVE_CC_PAYMENT,
  checkoutGetPaymentStatusDetails,
  checkoutProcessPaymentFulfilled,
  checkoutSetPaymentProcessing
} from '../../actions/checkoutPayment';
import CheckoutService from '../../services/CheckoutService';
import { getPaymentErrorMessage, paymentErrorMessageDefault } from '../../utils';

const request: EpicUtils.Request<CheckoutCCResponse, { sessionId: string }> = {
  type: CHECKOUT_COMPLETE_WINDCAVE_CC_PAYMENT,
  getData: ({ sessionId }, s) => CheckoutService.submitCreditCardPayment({
    onCoursePaymentSessionId: sessionId,
    paymentMethodId: null,
    transactionId: null,
    merchantReference: s.checkout.payment.merchantReference,
    origin: window.location.origin
  }),
  processData: (checkoutResponse, s, { sessionId }) => [
    checkoutGetPaymentStatusDetails(sessionId),
    checkoutProcessPaymentFulfilled(checkoutResponse.checkoutResponse),
    checkoutSetPaymentProcessing(false),
  ],
  processError: (response, { sessionId }) => {
    const actions: any = [
      checkoutSetPaymentProcessing(false),
      checkoutProcessPaymentFulfilled({
        paymentId: null,
        invoice: null,
      })
    ];

    if (response) {
      if (response.status === 400 && ['Unexpected request', 'Already in progress'].includes(response.data?.error)) {
        return [
          checkoutGetPaymentStatusDetails(sessionId)
        ];
      }
      if (Array.isArray(response.data)) {
        actions.push({
          type: SHOW_MESSAGE,
          payload: {
            message: getPaymentErrorMessage(response),
            persist: true
          }
        });
      } else {
        actions.push(
          ...FetchErrorHandler(response, getPaymentErrorMessage(response))
        );
      }
    } else {
      actions.push(...FetchErrorHandler(response, paymentErrorMessageDefault));
    }

    return actions;
  }
};

export const EpicCompleteWindcavePaymentRedirect: Epic<any, any> = EpicUtils.Create(request);