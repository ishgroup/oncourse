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
  CHECKOUT_PROCESS_SQUARE_CC_PAYMENT,
  checkoutClearPaymentStatus,
  checkoutGetPaymentStatusDetails,
  checkoutPaymentSetStatus,
  checkoutProcessPaymentFulfilled,
  checkoutSetPaymentProcessing,
  clearCcIframeUrl
} from '../../actions/checkoutPayment';
import CheckoutService from '../../services/CheckoutService';
import { getCheckoutModel, getPaymentErrorMessage, paymentErrorMessageDefault } from '../../utils';

const request: EpicUtils.Request<{ checkoutResponse: CheckoutCCResponse, sessionId: string }, { creditCardToken: string, verificationToken: string, }> = {
  type: CHECKOUT_PROCESS_SQUARE_CC_PAYMENT,
  getData: async ({ creditCardToken, verificationToken }, s) => {

    const checkoutModel = getCheckoutModel(
      s
    );

    const sessionResponse = await CheckoutService.createSession(checkoutModel, s.checkout.payment.sessionId);

    const checkoutResponse = await CheckoutService.submitCreditCardPayment({
      onCoursePaymentSessionId: sessionResponse.sessionId,
      cardDataToken: creditCardToken,
      secureCode: verificationToken,
      paymentMethodId: null,
      transactionId: null,
      merchantReference: sessionResponse.merchantReference,
      origin: window.location.origin
    });

    return { checkoutResponse, sessionId: checkoutResponse.paymentSystemSessionId };
  },
  processData: ({ checkoutResponse, sessionId }) => [
    checkoutGetPaymentStatusDetails(sessionId),
    checkoutProcessPaymentFulfilled(checkoutResponse.checkoutResponse),
    checkoutSetPaymentProcessing(false),
  ],
  processError: response => {
    const actions: any = [
      checkoutSetPaymentProcessing(false),
      checkoutClearPaymentStatus(),
      clearCcIframeUrl()
    ];

    if (response) {
      actions.push(
        checkoutPaymentSetStatus(
          "fail",
          response.status,
          response.statusText,
          { ...response.data, responseText: getPaymentErrorMessage(response) }
        )
      );
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

export const EpicCheckoutProcessSquareCCPayment: Epic<any, any> = EpicUtils.Create(request);