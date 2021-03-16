/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import {
  CLEAR_PAYROLL_PREPARED_WAGES,
  EXECUTE_PAYROLL,
  EXECUTE_PAYROLL_FULFILLED,
  PAYROLL_PROCESS_FINISHED
} from "../actions/index";
import { PayrollRequest } from "@api/model";
import PayrollService from "../services/PayrollService";
import { SHOW_MESSAGE, START_PROCESS } from "../../../../common/actions";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

let maxPayslipId;

const request: EpicUtils.Request<any, { entity: string; confirm: boolean; payrollRequest: PayrollRequest }> = {
  type: EXECUTE_PAYROLL,
  getData: ({ entity, confirm, payrollRequest }) => {
    if (entity === "Payslip") {
      maxPayslipId = null;
    } else {
      EntityService.getPlainRecords("Payslip", "createdOn",null, 1, null, "id", false)
        .then(response => { maxPayslipId = response.rows[0].id });
    }
    return PayrollService.execute(entity, confirm, payrollRequest);
  },
  processData: (processId: string, state, { entity }) => {
    return [
      {
        type: EXECUTE_PAYROLL_FULFILLED
      },
      {
        type: START_PROCESS,
        payload: {
          processId,
          actions: [
            {
              type: PAYROLL_PROCESS_FINISHED,
              payload: {
                maxPayslipId
              }
            },
            {
              type: SHOW_MESSAGE,
              payload: {
                message: "Tutor pay was generated",
                success: true
              }
            },
            ...(entity === "Payslip"
              ? [
                  {
                    type: GET_RECORDS_REQUEST,
                    payload: {
                      entity
                    }
                  }
                ]
              : []),
            {
              type: CLEAR_PAYROLL_PREPARED_WAGES
            }
          ]
        }
      }
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

export const EpicExecutePayroll: Epic<any, any> = EpicUtils.Create(request);
