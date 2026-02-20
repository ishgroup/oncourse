/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { PayrollRequest } from '@api/model';
import { startProcess } from '../../../../common/actions';
import FetchErrorHandler from '../../../../common/api/fetch-errors-handlers/FetchErrorHandler';
import { Create, Request } from '../../../../common/epics/EpicUtils';
import { CLEAR_PAYROLL_PREPARED_WAGES, PREPARE_PAYROLL, setPreparedPayroll } from '../actions';
import PayrollService from '../services/PayrollService';

const request: Request<string, { entity: string; payrollRequest: PayrollRequest }> = {
  type: PREPARE_PAYROLL,
  getData: ({ entity, payrollRequest }) => PayrollService.prepare(entity, payrollRequest),
  processData: (preparedWagesProcessId: string, s, { payrollRequest }) => {
    return [
      startProcess(preparedWagesProcessId, [
        setPreparedPayroll(preparedWagesProcessId, payrollRequest)
      ])
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

export const EpicPreparePayRoll = Create(request);
