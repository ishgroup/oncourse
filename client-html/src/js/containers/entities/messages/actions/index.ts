/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { MessageExtended } from "../../../../model/common/Message";

export const GET_MESSAGE_ITEM = _toRequestType("get/message");
export const GET_MESSAGE_ITEM_FULFILLED = FULFILLED(GET_MESSAGE_ITEM);

export const DELETE_MESSAGE_ITEM = _toRequestType("delete/message");
export const DELETE_MESSAGE_ITEM_FULFILLED = FULFILLED(DELETE_MESSAGE_ITEM);

export const SEND_MESSAGE = _toRequestType("post/sendMessage");

export const getMessage = (id: string) => ({
  type: GET_MESSAGE_ITEM,
  payload: id
});

export const removeMessage = (id: string) => ({
  type: DELETE_MESSAGE_ITEM,
  payload: id
});

export const sendMessage = (model: MessageExtended) => ({
  type: SEND_MESSAGE,
  payload: model
});
