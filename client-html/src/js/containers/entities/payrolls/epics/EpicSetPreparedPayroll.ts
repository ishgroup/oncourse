/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { PayrollRequest, WagesToProcess } from '@api/model';
import { initialize } from 'redux-form';
import FetchErrorHandler from '../../../../common/api/fetch-errors-handlers/FetchErrorHandler';
import { Create, Request } from '../../../../common/epics/EpicUtils';
import { PAYSLIP_GENERATE_FORM } from '../../payslips/components/PayslipGenerateDialog';
import { CLEAR_PAYROLL_PREPARED_WAGES, PREPARE_PAYROLL_FULFILLED, SET_PREPARED_PAYROLL } from '../actions';
import PayrollService from '../services/PayrollService';

const request: Request<WagesToProcess, { processId: string, payrollRequest: PayrollRequest }> = {
  type: SET_PREPARED_PAYROLL,
  getData: ({ processId }) => PayrollService.getPreparationResult(processId),
  processData: (preparedWages: WagesToProcess, s, { payrollRequest }) => {
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

export const EpicSetPreparedPayroll = Create(request);
