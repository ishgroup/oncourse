/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { closeListNestedEditRecord } from "../../../../common/components/list-view/actions";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { MessageExtended } from "../../../../model/common/Message";
import { SEND_MESSAGE } from "../actions";
import MessageService from "../services/MessageService";

const request: EpicUtils.Request<any, MessageExtended> = {
  type: SEND_MESSAGE,
  getData: (model, s) => {
    const { selection, searchQuery } = s.list;

    const requestModel = {
      ...model,
      searchQuery: { ...searchQuery },
      variables: model.bindings ? model.bindings.reduce((prev: any, cur) => {
        prev[cur.name] = cur.value;
        return prev;
      }, {}) : {},
    };

    if (!model.selectAll && Array.isArray(selection) && selection.length) {
      requestModel.searchQuery.search = `id in (${String(selection)})`;
    }

    delete requestModel.selectAll;
    delete requestModel.bindings;
    delete requestModel.recipientsCount;
    delete requestModel.messageType;

    return MessageService.sendMessage(model.recipientsCount, requestModel, model.messageType);
  },
  processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "All messages sent" }
      },
      closeListNestedEditRecord(0)
    ],
  processError: response => FetchErrorHandler(response, "Messages sending failed")
};

export const EpicSendMessage: Epic<any, any> = EpicUtils.Create(request);
