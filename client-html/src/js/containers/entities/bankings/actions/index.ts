import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const POST_RECONCILE_BANKING = _toRequestType("post/banking/reconcile");
export const POST_RECONCILE_BANKING_FULFILLED = FULFILLED(POST_RECONCILE_BANKING);

export const GET_DEPOSIT_PAYMENTS = _toRequestType("get/banking/depositPayments");
export const GET_DEPOSIT_PAYMENTS_FULFILLED = FULFILLED(GET_DEPOSIT_PAYMENTS);

export const GET_DEPOSIT_ACCOUNTS = _toRequestType("get/banking/depositAccounts");
export const GET_DEPOSIT_ACCOUNTS_FULFILLED = FULFILLED(GET_DEPOSIT_ACCOUNTS);

export const INIT_DEPOSIT = "init/deposit";

export const UPDATE_BANKING_ACCOUNT_ID = "action/banking/update/accountId";

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