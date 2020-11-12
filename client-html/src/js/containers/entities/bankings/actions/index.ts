import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { Banking } from "@api/model";

export const GET_BANKING_ITEM = _toRequestType("get/banking");
export const GET_BANKING_ITEM_FULFILLED = FULFILLED(GET_BANKING_ITEM);

export const DELETE_BANKING_ITEM = _toRequestType("delete/banking");
export const DELETE_BANKING_ITEM_FULFILLED = FULFILLED(DELETE_BANKING_ITEM);

export const UPDATE_BANKING_ITEM = _toRequestType("put/banking");
export const UPDATE_BANKING_ITEM_FULFILLED = FULFILLED(UPDATE_BANKING_ITEM);

export const CREATE_BANKING_ITEM = _toRequestType("post/banking");
export const CREATE_BANKING_ITEM_FULFILLED = FULFILLED(CREATE_BANKING_ITEM);

export const POST_RECONCILE_BANKING = _toRequestType("post/banking/reconcile");
export const POST_RECONCILE_BANKING_FULFILLED = FULFILLED(POST_RECONCILE_BANKING);

export const GET_DEPOSIT_PAYMENTS = _toRequestType("get/banking/depositPayments");
export const GET_DEPOSIT_PAYMENTS_FULFILLED = FULFILLED(GET_DEPOSIT_PAYMENTS);

export const GET_DEPOSIT_ACCOUNTS = _toRequestType("get/banking/depositAccounts");
export const GET_DEPOSIT_ACCOUNTS_FULFILLED = FULFILLED(GET_DEPOSIT_ACCOUNTS);

export const INIT_DEPOSIT = "init/deposit";

export const UPDATE_BANKING_ACCOUNT_ID = "action/banking/update/accountId";

export const UPDATE_BANKING_SITE_ID = "action/banking/update/administrationCenterId";

export const getBanking = (id: string) => ({
  type: GET_BANKING_ITEM,
  payload: id
});

export const removeBanking = (id: string) => ({
  type: DELETE_BANKING_ITEM,
  payload: id
});

export const updateBanking = (id: string, banking: Banking) => ({
  type: UPDATE_BANKING_ITEM,
  payload: { id, banking }
});

export const createBanking = (banking: Banking) => ({
  type: CREATE_BANKING_ITEM,
  payload: { banking }
});

export const reconcileBanking = (ids: number[]) => ({
  type: POST_RECONCILE_BANKING,
  payload: { ids }
});

export const getDepositPayments = (accountId: number, siteId: number) => ({
  type: GET_DEPOSIT_PAYMENTS,
  payload: { accountId, siteId }
});

export const getDepositAccounts = () => ({
  type: GET_DEPOSIT_ACCOUNTS
});

export const initDeposit = () => ({
  type: INIT_DEPOSIT
});

export const updateBankingAccountId = (id: number) => ({
  type: UPDATE_BANKING_ACCOUNT_ID,
  payload: id
});
