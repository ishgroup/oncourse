/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { _toRequestType } from "../../../../common/actions/ActionUtils";
import { MessageExtended } from "../../../../model/common/Message";

export const SEND_MESSAGE = _toRequestType("post/sendMessage");

export const sendMessage = (model: MessageExtended, selection: string[]) => ({
  type: SEND_MESSAGE,
  payload: { model, selection }
});