import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { Payslip, PayslipStatus } from "@api/model";

export const UPDATE_PAYSLIP_ITEM = _toRequestType("put/payslip");
export const UPDATE_PAYSLIP_ITEM_FULFILLED = FULFILLED(UPDATE_PAYSLIP_ITEM);

export const POST_PAYSLIP_EXECUTE = _toRequestType("post/payslip/execute");
export const POST_PAYSLIP_EXECUTE_FULFILLED = FULFILLED(POST_PAYSLIP_EXECUTE);

export const updatePayslip = (id: string, payslip: Payslip) => ({
  type: UPDATE_PAYSLIP_ITEM,
  payload: { id, payslip }
});

export const executePayslip = (ids: number[], status: PayslipStatus) => ({
  type: POST_PAYSLIP_EXECUTE,
  payload: { ids, status }
});