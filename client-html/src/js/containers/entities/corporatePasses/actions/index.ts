import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { CorporatePass } from "@api/model";

export const GET_CORPORATE_PASS_ITEM = _toRequestType("get/corporatepass");
export const GET_CORPORATE_PASS_ITEM_FULFILLED = FULFILLED(GET_CORPORATE_PASS_ITEM);

export const DELETE_CORPORATE_PASS_ITEM = _toRequestType("delete/corporatepass");
export const DELETE_CORPORATE_PASS_ITEM_FULFILLED = FULFILLED(DELETE_CORPORATE_PASS_ITEM);

export const UPDATE_CORPORATE_PASS_ITEM = _toRequestType("put/corporatepass");
export const UPDATE_CORPORATE_PASS_ITEM_FULFILLED = FULFILLED(UPDATE_CORPORATE_PASS_ITEM);

export const CREATE_CORPORATE_PASS_ITEM = _toRequestType("post/corporatepass");
export const CREATE_CORPORATE_PASS_ITEM_FULFILLED = FULFILLED(CREATE_CORPORATE_PASS_ITEM);

export const getCorporatePass = (id: string) => ({
  type: GET_CORPORATE_PASS_ITEM,
  payload: id
});

export const removeCorporatePass = (id: string) => ({
  type: DELETE_CORPORATE_PASS_ITEM,
  payload: id
});

export const updateCorporatePass = (id: string, corporatePass: CorporatePass) => ({
  type: UPDATE_CORPORATE_PASS_ITEM,
  payload: { id, corporatePass }
});

export const createCorporatePass = (corporatePass: CorporatePass) => ({
  type: CREATE_CORPORATE_PASS_ITEM,
  payload: { corporatePass }
});
