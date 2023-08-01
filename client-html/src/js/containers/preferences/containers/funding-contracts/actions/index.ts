import { FundingSource } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../../common/actions/ActionUtils";
import { ApiMethods } from "../../../../../model/common/apiHandlers";

export const GET_FUNDING_CONTACTS_REQUEST = _toRequestType("get/fundingcontracts");
export const GET_FUNDING_CONTACTS_FULFILLED = FULFILLED(GET_FUNDING_CONTACTS_REQUEST);

export const SAVE_FUNDING_CONTACTS_REQUEST = _toRequestType("save/fundingcontracts");
export const SAVE_FUNDING_CONTACTS_FULFILLED = FULFILLED(SAVE_FUNDING_CONTACTS_REQUEST);

export const DELETE_FUNDING_CONTACT_REQUEST = _toRequestType("delete/fundingcontract");
export const DELETE_FUNDING_CONTACT_FULFILLED = FULFILLED(DELETE_FUNDING_CONTACT_REQUEST);

export const getFundingContracts = () => ({
  type: GET_FUNDING_CONTACTS_REQUEST
});

export const saveFundingContracts = (items: FundingSource[], method: ApiMethods) => ({
  type: SAVE_FUNDING_CONTACTS_REQUEST,
  payload: { items, method }
});

export const deleteFundingContract = id => ({
  type: DELETE_FUNDING_CONTACT_REQUEST,
  payload: { id }
});
