import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { PayslipStatus } from "@api/model";

export const POST_PAYSLIP_EXECUTE = _toRequestType("post/payslip/execute");
export const POST_PAYSLIP_EXECUTE_FULFILLED = FULFILLED(POST_PAYSLIP_EXECUTE);

export const executePayslip = (ids: number[], status: PayslipStatus) => ({
  type: POST_PAYSLIP_EXECUTE,
  payload: { ids, status }
});