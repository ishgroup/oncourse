/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { Account } from "@api/model";
import { change } from "redux-form";
import { format } from "date-fns";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import AccountService from "../../accounts/services/AccountService";
import { GET_DEPOSIT_ACCOUNTS, GET_DEPOSIT_ACCOUNTS_FULFILLED } from "../actions";
import { IAction } from "../../../../common/actions/IshAction";
import { FETCH_FAIL } from "../../../../common/actions";
import { YYYY_MM_DD_MINUSED } from "../../../../common/utils/dates/format";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: GET_DEPOSIT_ACCOUNTS,
  getData: () => AccountService.getDepositAccounts(),
  processData: (accounts: Account[]) => {
    let actions: IAction<any>[];
    if (accounts.length > 0) {
      const account = accounts[0];
      actions = [
        {
          type: GET_DEPOSIT_ACCOUNTS_FULFILLED,
          payload: { accounts, selectedAccountId: account.id }
        },
        change(LIST_EDIT_VIEW_FORM_NAME, "settlementDate", format(new Date(), YYYY_MM_DD_MINUSED))
      ];
    } else {
      actions = [
        {
          type: GET_DEPOSIT_ACCOUNTS_FULFILLED,
          payload: { accounts, selectedAccountId: null }
        },
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
        type: GET_DEPOSIT_ACCOUNTS_FULFILLED,
        payload: { accounts: [], selectedAccountId: null }
      },
      ...FetchErrorHandler(response)]
};

export const EpicGetDepositAccounts: Epic<any, any> = EpicUtils.Create(request);
