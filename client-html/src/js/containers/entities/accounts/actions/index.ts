/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Account, AccountType } from "@api/model";
import { Dispatch } from "redux";
import { _toRequestType } from "../../../../common/actions/ActionUtils";
import { getCommonPlainRecords, setCommonPlainSearch } from "../../../../common/actions/CommonPlainRecordsActions";
import { PLAIN_LIST_MAX_PAGE_SIZE } from "../../../../constants/Config";

export const GET_ACCOUNT_ITEM = _toRequestType("get/account");

export const DELETE_ACCOUNT_ITEM = _toRequestType("delete/account");

export const UPDATE_ACCOUNT_ITEM = _toRequestType("put/account");

export const CREATE_ACCOUNT_ITEM = _toRequestType("post/account");

export const getAccount = (id: string) => ({
  type: GET_ACCOUNT_ITEM,
  payload: id
});

export const removeAccount = (id: string) => ({
  type: DELETE_ACCOUNT_ITEM,
  payload: id
});

export const updateAccount = (id: string, account: Account) => ({
  type: UPDATE_ACCOUNT_ITEM,
  payload: { id, account }
});

export const createAccount = (account: Account) => ({
  type: CREATE_ACCOUNT_ITEM,
  payload: { account }
});

export const getPlainAccounts = (dispatch: Dispatch, type?: AccountType) => {
  dispatch(setCommonPlainSearch("Account", `isEnabled is true${type ? ` and type is ${type}` : ""}`));
  dispatch(getCommonPlainRecords("Account", 0, "description,accountCode,type,tax.id", true, "description", PLAIN_LIST_MAX_PAGE_SIZE));
};
