/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { _toRequestType } from '../../../../common/actions/ActionUtils';
import { MessageExtended } from '../../../../model/common/Message';

export const SEND_MESSAGE = _toRequestType("post/message/send");

export const sendMessage = (model: MessageExtended, selection: string[]) => ({
  type: SEND_MESSAGE,
  payload: { model, selection }
});

export const POLL_SEND_MESSAGE_PROCESS = "messages/send/process/poll";

export const INTERRUPT_SEND_MESSAGE = _toRequestType("delete/message/send/process");

export const pollSendMessageProcess = (processId: string) => ({
  type: POLL_SEND_MESSAGE_PROCESS,
  payload: { processId }
});

export const interruptSendMessage = (processId: string) => ({
  type: INTERRUPT_SEND_MESSAGE,
  payload: { processId }
});
