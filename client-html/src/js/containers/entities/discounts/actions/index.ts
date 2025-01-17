import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const GET_DISCOUNT_CONTACT_RELATION_TYPES = _toRequestType("get/discount/contactRelationTypes");
export const GET_DISCOUNT_CONTACT_RELATION_TYPES_FULFILLED = FULFILLED(GET_DISCOUNT_CONTACT_RELATION_TYPES);

export const GET_DISCOUNT_COS_ACCOUNTS = _toRequestType("get/discount/cosAccounts");
export const GET_DISCOUNT_COS_ACCOUNTS_FULFILLED = FULFILLED(GET_DISCOUNT_COS_ACCOUNTS);

export const getDiscountContactRelationTypes = () => ({
  type: GET_DISCOUNT_CONTACT_RELATION_TYPES
});

export const getDiscountCosAccounts = () => ({
  type: GET_DISCOUNT_COS_ACCOUNTS
});