/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import { sendMessageFailed, setSendMessageProcess } from '../../../../common/actions';
import FetchErrorHandler from '../../../../common/api/fetch-errors-handlers/FetchErrorHandler';
import * as EpicUtils from '../../../../common/epics/EpicUtils';
import { MessageExtended } from '../../../../model/common/Message';
import { interruptSendMessage, pollSendMessageProcess, SEND_MESSAGE } from '../actions';
import MessageService from '../services/MessageService';
import { getMessageRequestModel } from '../utils';

const request: EpicUtils.Request<any, { model: MessageExtended, selection: string[] }> = {
  type: SEND_MESSAGE,
  hideLoadIndicator: true,
  getData: ({ model, selection }, s) => MessageService.sendMessage(model.recipientsCount, getMessageRequestModel(model, selection, s.list.searchQuery), model.messageType),
  processData: (processId: string, s) => {
    const { sending, processId: currentProcessId } = s.sendMessage;

    // Dialog was closed while the request was in flight - nobody waits for this process anymore
    if (!sending || currentProcessId) {
      return [interruptSendMessage(processId)];
    }

    return [
      setSendMessageProcess(processId),
      pollSendMessageProcess(processId)
    ];
  },
  processError: response => [
    sendMessageFailed(),
    ...FetchErrorHandler(response, "Messages sending failed")
  ]
};

export const EpicSendMessage: Epic<any, any> = EpicUtils.Create(request);
