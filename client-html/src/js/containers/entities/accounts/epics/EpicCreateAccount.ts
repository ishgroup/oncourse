/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import AccountService from "../services/AccountService";
import { CREATE_ACCOUNT_ITEM } from "../actions/index";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { initialize } from "redux-form";
import { Account } from "@api/model";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../../common/components/list-view/actions/index";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

let savedItem: Account;

const request: EpicUtils.Request = {
  type: CREATE_ACCOUNT_ITEM,
  getData: payload => {
    savedItem = payload.account;
    return AccountService.createAccount(payload.account);
  },
  processData: () => {
    return [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Account Record created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Account", listUpdate: true }
      },
      clearListNestedEditRecord(0),
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: response => [
    ...FetchErrorHandler(response, "Account Record was not created"),
    initialize(LIST_EDIT_VIEW_FORM_NAME, savedItem)
  ]
};

export const EpicCreateAccount: Epic<any, any> = EpicUtils.Create(request);
