/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { SearchQuery, SendMessageRequest } from "@api/model";
import { MessageExtended } from "../../../../model/common/Message";

export const getMessageRequestModel = (message: MessageExtended, selection: string[], searchQuery: SearchQuery): SendMessageRequest => {
  const requestModel = {
    ...message,
    searchQuery: { ...searchQuery },
    variables: message.bindings ? message.bindings.reduce((prev: any, cur) => {
      prev[cur.name] = cur.value ?? (cur.type === "Text" ? "" : null);
      return prev;
    }, {}) : {}
  };

  if (!message.selectAll && Array.isArray(selection) && selection.length) {
    requestModel.searchQuery.search = `id in (${String(selection)})`;
  }

  delete requestModel.selectAll;
  delete requestModel.bindings;
  delete requestModel.recipientsCount;
  delete requestModel.messageType;

  return requestModel;
};
