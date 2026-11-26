/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AssistantReply } from "@api/model";
import { _toRequestType, FULFILLED, REJECTED } from "../../../common/actions/ActionUtils";

export const ASSISTANT_SET_OPEN = "assistant/setOpen";

export const SEND_ASSISTANT_MESSAGE = _toRequestType("post/assistant/message");
export const SEND_ASSISTANT_MESSAGE_FULFILLED = FULFILLED(SEND_ASSISTANT_MESSAGE);
export const SEND_ASSISTANT_MESSAGE_REJECTED = REJECTED(SEND_ASSISTANT_MESSAGE);

export const setAssistantOpen = (open: boolean) => ({
  type: ASSISTANT_SET_OPEN,
  payload: open
});

export const sendAssistantMessage = (text: string) => ({
  type: SEND_ASSISTANT_MESSAGE,
  payload: { text }
});

export const sendAssistantMessageFulfilled = (reply: AssistantReply) => ({
  type: SEND_ASSISTANT_MESSAGE_FULFILLED,
  payload: reply
});

export const sendAssistantMessageRejected = (message: string) => ({
  type: SEND_ASSISTANT_MESSAGE_REJECTED,
  payload: { message }
});
