import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { Payslip, PayslipStatus } from "@api/model";

export const GET_PAYSLIP_ITEM = _toRequestType("get/payslip");
export const GET_PAYSLIP_ITEM_FULFILLED = FULFILLED(GET_PAYSLIP_ITEM);

export const DELETE_PAYSLIP_ITEM = _toRequestType("delete/payslip");
export const DELETE_PAYSLIP_ITEM_FULFILLED = FULFILLED(DELETE_PAYSLIP_ITEM);

export const UPDATE_PAYSLIP_ITEM = _toRequestType("put/payslip");
export const UPDATE_PAYSLIP_ITEM_FULFILLED = FULFILLED(UPDATE_PAYSLIP_ITEM);

export const CREATE_PAYSLIP_ITEM = _toRequestType("post/payslip");
export const CREATE_PAYSLIP_ITEM_FULFILLED = FULFILLED(CREATE_PAYSLIP_ITEM);

export const POST_PAYSLIP_EXECUTE = _toRequestType("post/payslip/execute");
export const POST_PAYSLIP_EXECUTE_FULFILLED = FULFILLED(POST_PAYSLIP_EXECUTE);

export const getPayslip = (id: string) => ({
  type: GET_PAYSLIP_ITEM,
  payload: id
});

export const removePayslip = (id: string) => ({
  type: DELETE_PAYSLIP_ITEM,
  payload: id
});

export const updatePayslip = (id: string, payslip: Payslip) => ({
  type: UPDATE_PAYSLIP_ITEM,
  payload: { id, payslip }
});

export const createPayslip = (payslip: Payslip) => ({
  type: CREATE_PAYSLIP_ITEM,
  payload: { payslip }
});

export const executePayslip = (ids: number[], status: PayslipStatus) => ({
  type: POST_PAYSLIP_EXECUTE,
  payload: { ids, status }
});
