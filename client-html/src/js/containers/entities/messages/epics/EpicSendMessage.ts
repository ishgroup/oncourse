/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import {
  clearProcess,
  closeSendMessage,
  FETCH_SUCCESS,
  SEND_MESSAGE_FAILED,
  START_PROCESS,
  UPDATE_PROCESS
} from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { MessageExtended } from "../../../../model/common/Message";
import { SEND_MESSAGE } from "../actions";
import MessageService from "../services/MessageService";
import { getMessageRequestModel } from "../utils";

const request: EpicUtils.Request<any, { model: MessageExtended, selection: string[] }> = {
  type: SEND_MESSAGE,
  hideLoadIndicator: true,
  getData: ({ model, selection }, s) => MessageService.sendMessage(model.recipientsCount, getMessageRequestModel(model, selection, s.list.searchQuery), model.messageType),
  processData: (processId: string) => [
    {
      type: UPDATE_PROCESS,
      payload: { processId }
    },
    {
      type: START_PROCESS,
      payload: {
        processId,
        actions: [
          closeSendMessage(),
          {
            type: FETCH_SUCCESS,
            payload: { message: "All messages sent" }
          }
        ],
        actionsOnFail: [
          { type: SEND_MESSAGE_FAILED }
        ]
      }
    }
  ],
  processError: response => [
    { type: SEND_MESSAGE_FAILED },
    clearProcess(),
    ...FetchErrorHandler(response, "Messages sending failed")
  ]
};

export const EpicSendMessage: Epic<any, any> = EpicUtils.Create(request);
