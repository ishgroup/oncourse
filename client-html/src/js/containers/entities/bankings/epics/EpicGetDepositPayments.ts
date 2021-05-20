/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { change } from "redux-form";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import BankingService from "../services/BankingService";
import { GET_DEPOSIT_PAYMENTS, GET_DEPOSIT_PAYMENTS_FULFILLED } from "../actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { FETCH_FAIL } from "../../../../common/actions";
import { IAction } from "../../../../common/actions/IshAction";

const request: EpicUtils.Request = {
  type: GET_DEPOSIT_PAYMENTS,
  getData: ({ accountId, siteId }) => BankingService.getDepositPayments(accountId, siteId),
  processData: (response: any) => {
    const payments = response.map(v => {
      v.selected = true;
      return v;
    });

    let actions: IAction<any>[];

    if (payments.length > 0) {
      actions = [
        {
          type: GET_DEPOSIT_PAYMENTS_FULFILLED,
          payload: { payments }
        },
        change(LIST_EDIT_VIEW_FORM_NAME, "payments", payments)
      ];
    } else {
      actions = [
        {
          type: GET_DEPOSIT_PAYMENTS_FULFILLED,
          payload: { payments }
        },
        change(LIST_EDIT_VIEW_FORM_NAME, "payments", payments),
        {
          type: FETCH_FAIL,
          payload: { message: "There are no items waiting to be banked." }
        }
      ];
    }
    return actions;
  },
  processError: response => [
      {
        type: GET_DEPOSIT_PAYMENTS_FULFILLED,
        payload: { payments: [] }
      },
      change(LIST_EDIT_VIEW_FORM_NAME, "payments", []),
      ...FetchErrorHandler(response)]
};

export const EpicGetDepositPayments: Epic<any, any> = EpicUtils.Create(request);
