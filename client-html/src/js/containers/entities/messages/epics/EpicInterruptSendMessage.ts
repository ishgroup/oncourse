/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import * as EpicUtils from '../../../../common/epics/EpicUtils';
import ProcessService from '../../../../common/services/ProcessService';
import { INTERRUPT_SEND_MESSAGE } from '../actions';

const request: EpicUtils.Request<any, { processId: string }> = {
  type: INTERRUPT_SEND_MESSAGE,
  hideLoadIndicator: true,
  getData: ({ processId }) => ProcessService.interruptProcess(processId),
  processData: () => [],
  // The user has already closed the dialog, nothing to report
  processError: () => []
};

export const EpicInterruptSendMessage: Epic<any, any> = EpicUtils.Create(request);
