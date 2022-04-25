import { Discount } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const GET_DISCOUNT_ITEM = _toRequestType("get/discount");
export const GET_DISCOUNT_ITEM_FULFILLED = FULFILLED(GET_DISCOUNT_ITEM);

export const DELETE_DISCOUNT_ITEM = _toRequestType("delete/discount");
export const DELETE_DISCOUNT_ITEM_FULFILLED = FULFILLED(DELETE_DISCOUNT_ITEM);

export const UPDATE_DISCOUNT_ITEM = _toRequestType("put/discount");
export const UPDATE_DISCOUNT_ITEM_FULFILLED = FULFILLED(UPDATE_DISCOUNT_ITEM);

export const CREATE_DISCOUNT_ITEM = _toRequestType("post/discount");
export const CREATE_DISCOUNT_ITEM_FULFILLED = FULFILLED(CREATE_DISCOUNT_ITEM);

export const GET_DISCOUNT_CONTACT_RELATION_TYPES = _toRequestType("get/discount/contactRelationTypes");
export const GET_DISCOUNT_CONTACT_RELATION_TYPES_FULFILLED = FULFILLED(GET_DISCOUNT_CONTACT_RELATION_TYPES);

export const GET_DISCOUNT_COS_ACCOUNTS = _toRequestType("get/discount/cosAccounts");
export const GET_DISCOUNT_COS_ACCOUNTS_FULFILLED = FULFILLED(GET_DISCOUNT_COS_ACCOUNTS);

export const getDiscount = (id: string) => ({
  type: GET_DISCOUNT_ITEM,
  payload: id
});

export const removeDiscount = (id: string) => ({
  type: DELETE_DISCOUNT_ITEM,
  payload: id
});

export const updateDiscount = (id: string, discount: Discount) => ({
  type: UPDATE_DISCOUNT_ITEM,
  payload: { id, discount }
});

export const createDiscount = (discount: Discount) => ({
  type: CREATE_DISCOUNT_ITEM,
  payload: discount
});

export const getDiscountContactRelationTypes = () => ({
  type: GET_DISCOUNT_CONTACT_RELATION_TYPES
});

export const getDiscountCosAccounts = () => ({
  type: GET_DISCOUNT_COS_ACCOUNTS
});
