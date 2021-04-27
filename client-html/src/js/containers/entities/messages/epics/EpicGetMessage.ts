/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { Message } from "@api/model";
import { initialize } from "redux-form";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_MESSAGE_ITEM, GET_MESSAGE_ITEM_FULFILLED } from "../actions/index";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import MessageService from "../services/MessageService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: GET_MESSAGE_ITEM,
  getData: (id: number) => MessageService.getMessage(id),
  processData: (message: Message) => [
      {
        type: GET_MESSAGE_ITEM_FULFILLED,
        payload: { message }
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: message, name: `${message.sentToContactFullname} (${message.subject})` }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, message)
    ],
  processError: response => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicGetMessage: Epic<any, any> = EpicUtils.Create(request);
