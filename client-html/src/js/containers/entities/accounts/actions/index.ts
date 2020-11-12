/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { Account } from "@api/model";

export const GET_ACCOUNT_ITEM = _toRequestType("get/account");

export const DELETE_ACCOUNT_ITEM = _toRequestType("delete/account");

export const UPDATE_ACCOUNT_ITEM = _toRequestType("put/account");

export const CREATE_ACCOUNT_ITEM = _toRequestType("post/account");

export const GET_PLAIN_ACCOUNTS = _toRequestType("get/plain/account");
export const GET_PLAIN_ACCOUNTS_FULFILLED = FULFILLED(GET_PLAIN_ACCOUNTS);

export const GET_INCOME_ACCOUNTS = _toRequestType("get/account/incomeAccounts");
export const GET_INCOME_ACCOUNTS_FULFILLED = FULFILLED(GET_INCOME_ACCOUNTS);

export const GET_LIABILITY_ACCOUNTS = _toRequestType("get/account/liabilityAccounts");
export const GET_LIABILITY_ACCOUNTS_FULFILLED = FULFILLED(GET_LIABILITY_ACCOUNTS);

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

export const getPlainAccounts = () => ({
  type: GET_PLAIN_ACCOUNTS,
  payload: {}
});

export const getIncomeAccounts = () => ({
  type: GET_INCOME_ACCOUNTS
});

export const getLiabilityAccounts = () => ({
  type: GET_LIABILITY_ACCOUNTS
});
