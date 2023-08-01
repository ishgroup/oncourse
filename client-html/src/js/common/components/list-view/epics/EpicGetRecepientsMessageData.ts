/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import { MessageType, Recipients, SearchQuery } from "@api/model";
import { Epic } from "redux-observable";
import MessageService from "../../../../containers/entities/messages/services/MessageService";
import { MessageData } from "../../../../model/common/Message";
import * as EpicUtils from "../../../epics/EpicUtils";
import { GET_RECIPIENTS_MESSAGE_DATA, setRecipientsMessageData } from "../actions";

const request:
  EpicUtils.Request<Recipients, {
    entityName: string,
    messageType: MessageType,
    searchQuery: SearchQuery,
    selection: string[],
    templateId: number
  }> = {
  type: GET_RECIPIENTS_MESSAGE_DATA,
  hideLoadIndicator: true,
  getData: ({
              entityName, messageType, searchQuery, selection, templateId
            }) => {
    if (Array.isArray(selection) && selection.length) {
      const selectionSearch: SearchQuery = {
        search: `id in (${String(selection)})`,
        filter: "",
        tagGroups: []
      };

      return MessageService.getRecipients(entityName, messageType, selectionSearch, templateId);
    }
    return MessageService.getRecipients(entityName, messageType, searchQuery, templateId);
  },
  processData: (r: Recipients, s, {selection, messageType}) => {
    const payload: MessageData = {
      [(Array.isArray(selection) && selection.length) ? "selected" : "filtered"]: {
        [messageType]: r,
      }
    };

    return [
      setRecipientsMessageData(payload)
    ];
  }
};

export const EpicGetRecepientsMessageData: Epic<any, any> = EpicUtils.Create(request);

