/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { PayrollRequest } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const PREPARE_PAYROLL = _toRequestType("post/list/option/payroll");
export const PREPARE_PAYROLL_FULFILLED = FULFILLED(PREPARE_PAYROLL);

export const EXECUTE_PAYROLL = _toRequestType("put/list/option/payroll");
export const EXECUTE_PAYROLL_FULFILLED = FULFILLED(EXECUTE_PAYROLL);
export const PAYROLL_PROCESS_FINISHED = "payroll/process/finished";

export const CLEAR_PAYROLL_PREPARED_WAGES = "clear/list/option/payroll/wages";

export const preparePayroll = (entity: string, payrollRequest: PayrollRequest) => ({
  type: PREPARE_PAYROLL,
  payload: { entity, payrollRequest }
});

export const executePayroll = (entity: string, confirm: boolean, payrollRequest: PayrollRequest) => ({
  type: EXECUTE_PAYROLL,
  payload: { entity, confirm, payrollRequest }
});

export const clearPayrollPreparedWages = () => ({
  type: CLEAR_PAYROLL_PREPARED_WAGES
});
