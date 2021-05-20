/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { Account, Transaction } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_TRANSACTION_ITEM } from "../actions";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { getEntityItemById } from "../../common/entityItemsService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any,  any> = {
  type: GET_TRANSACTION_ITEM,
  getData: (id: number) => getEntityItemById("AccountTransaction", id),
  processData: (transaction: Transaction, state) => {
    let name: any = transaction.fromAccount;

    if (state.accounts.items) {
      const account = state.accounts.items.find((acc: Account) => acc.id === transaction.fromAccount);

      if (account) {
        name = `${account.description} ${account.accountCode}`;
      }
    }

    return [
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: transaction, name }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, transaction)
    ];
  },
  processError: response => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicGetTransaction: Epic<any, any> = EpicUtils.Create(request);
