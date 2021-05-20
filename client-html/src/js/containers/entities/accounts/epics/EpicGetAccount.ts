/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_ACCOUNT_ITEM } from "../actions/index";
import { Account } from "@api/model";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions/index";
import { initialize } from "redux-form";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { getEntityItemById } from "../../common/entityItemsService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: GET_ACCOUNT_ITEM,
  getData: (id: number) => getEntityItemById("Account", id),
  processData: (account: Account) => {
    return [
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: account, name: `${account.accountCode} ${account.description}` }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, account)
    ];
  },
  processError: response => {
    return [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)];
  }
};

export const EpicGetAccount: Epic<any, any> = EpicUtils.Create(request);
