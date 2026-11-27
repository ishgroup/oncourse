/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from "../../../common/actions/IshAction";
import { GET_IS_LOGGED_FULFILLED } from "../../preferences/actions";
import {
  ASSISTANT_SET_OPEN,
  SEND_ASSISTANT_MESSAGE,
  SEND_ASSISTANT_MESSAGE_FULFILLED,
  SEND_ASSISTANT_MESSAGE_REJECTED
} from "../actions";
import { AssistantChatMessage, AssistantState } from "../model";

export const AssistantInitialState: AssistantState = {
  open: false,
  conversationId: null,
  messages: [],
  pending: false,
  error: null
};

let messageCounter = 0;

const chatMessage = (role: AssistantChatMessage["role"], text: string): AssistantChatMessage => ({
  id: `assistant-message-${++messageCounter}`,
  role,
  text
});

export const assistantReducer = (current: AssistantState | undefined, action: IAction<any>): AssistantState => {
  const state = current || AssistantInitialState;

  switch (action.type) {
    case ASSISTANT_SET_OPEN:
      return {
        ...state,
        open: action.payload
      };

    case SEND_ASSISTANT_MESSAGE:
      // a message sent while waiting for a reply is ignored, as by the epic
      if (state.pending) {
        return state;
      }
      return {
        ...state,
        messages: [...state.messages, chatMessage("user", action.payload.text)],
        pending: true,
        error: null
      };

    case SEND_ASSISTANT_MESSAGE_FULFILLED:
      // nothing is waited for after logout, a late reply belongs to the previous user
      if (!state.pending) {
        return state;
      }
      return {
        ...state,
        conversationId: action.payload.conversationId,
        messages: [...state.messages, chatMessage("assistant", action.payload.text)],
        pending: false,
        error: null
      };

    case SEND_ASSISTANT_MESSAGE_REJECTED:
      if (!state.pending) {
        return state;
      }
      return {
        ...state,
        pending: false,
        error: action.payload.message
      };

    case GET_IS_LOGGED_FULFILLED:
      // nothing is kept for the next user after logout
      return action.payload === false ? AssistantInitialState : state;

    default:
      return state;
  }
};
