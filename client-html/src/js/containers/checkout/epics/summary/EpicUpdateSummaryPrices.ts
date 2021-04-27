/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { SHOW_MESSAGE } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { checkoutSetHasErrors } from "../../actions";
import {
  CHECKOUT_UPDATE_SUMMARY_PRICES,
  CHECKOUT_UPDATE_SUMMARY_PRICES_FULFILLED,
  checkoutUncheckSummaryItems, checkoutUpdateSummaryPrices
} from "../../actions/checkoutSummary";
import CheckoutService from "../../services/CheckoutService";
import { getCheckoutModel } from "../../utils";

const request: EpicUtils.Request = {
  type: CHECKOUT_UPDATE_SUMMARY_PRICES,
  hideLoadIndicator: true,
  getData: (p, s) => {
    const model = getCheckoutModel(s.checkout, [], null, {}, true);
    return CheckoutService.checkoutSubmitPayment(
      model,
      true,
      null,
      window.location.origin
    );
  },
  processData: res => [
    {
      type: CHECKOUT_UPDATE_SUMMARY_PRICES_FULFILLED,
      payload: {
        invoice: res.invoice
      }
    }
  ],
  processError: res => {
    if (res && res.data && Array.isArray(res.data) ) {
      const summaryNodesErrors = res.data.filter(d => d.nodeId && d.itemId);
      return [
        ...summaryNodesErrors.length ? [
          checkoutUncheckSummaryItems(summaryNodesErrors.map(({ nodeId, itemId }) => ({
            nodeId,
            itemId
          }))),
          checkoutUpdateSummaryPrices()
        ] : [],
        {
          type: SHOW_MESSAGE,
          payload: {
            message: res.data.reduce((p, c, i) => p + c.error + (i === res.data.length - 1 ? "" : "\n\n"), "")
          },
        },
        checkoutSetHasErrors(true)
      ];
    }
    return FetchErrorHandler(res, "Failed to update summary prices");
  }
};

export const EpicUpdateSummaryPrices: Epic<any, any> = EpicUtils.Create(request);
