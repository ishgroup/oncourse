/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import { Epic } from "redux-observable";
import { MessageType, Recipients, SearchQuery } from "@api/model";
import { MessageData } from "../../../../model/common/Message";
import * as EpicUtils from "../../../epics/EpicUtils";
import MessageService from "../../../../containers/entities/messages/services/MessageService";
import { GET_RECIPIENTS_MESSAGE_DATA, setRecipientsMessageData } from "../actions";

const request:
  EpicUtils.Request<Recipients, { entityName: string, messageType: MessageType, searchQuery: SearchQuery, selection: string[] }> = {
    type: GET_RECIPIENTS_MESSAGE_DATA,
    hideLoadIndicator: true,
    getData: ({
     entityName, messageType, searchQuery, selection
    }) => {
      if (Array.isArray(selection) && selection.length) {
        const selectionSearch: SearchQuery = {
          search: `id in (${String(selection)})`,
          filter: "",
          tagGroups: []
        };

        return MessageService.getRecipients(entityName, messageType, selectionSearch);
      }
      return MessageService.getRecipients(entityName, messageType, searchQuery);
    },
    processData: (r: Recipients, s, { selection, messageType }) => {
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

