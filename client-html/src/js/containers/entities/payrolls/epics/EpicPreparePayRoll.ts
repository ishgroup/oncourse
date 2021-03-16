/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { CLEAR_PAYROLL_PREPARED_WAGES, PREPARE_PAYROLL, PREPARE_PAYROLL_FULFILLED } from "../actions/index";
import { PayrollRequest, WagesToProcess } from "@api/model";
import PayrollService from "../services/PayrollService";
import { initialize } from "redux-form";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { PAYSLIP_GENERATE_FORM } from "../../payslips/components/PayslipGenerateDialog";

const request: EpicUtils.Request<any, { entity: string; payrollRequest: PayrollRequest }> = {
  type: PREPARE_PAYROLL,
  getData: ({ entity, payrollRequest }) => PayrollService.prepare(entity, payrollRequest),
  processData: (preparedWages: WagesToProcess, state, { payrollRequest }) => {
    return [
      {
        type: PREPARE_PAYROLL_FULFILLED,
        payload: { preparedWages }
      },
      initialize(PAYSLIP_GENERATE_FORM, payrollRequest)
    ];
  },
  processError: response => {
    return [
      {
        type: CLEAR_PAYROLL_PREPARED_WAGES
      },
      ...FetchErrorHandler(response)
    ];
  }
};

export const EpicPreparePayRoll: Epic<any, any> = EpicUtils.Create(request);
