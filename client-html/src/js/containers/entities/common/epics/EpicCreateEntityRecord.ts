/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { processNotesAsyncQueue } from "../../../../common/components/form/notes/utils";
import { Create, Request } from "../../../../common/epics/EpicUtils";
import { EntityName } from "../../../../model/entities/common";
import { CREATE_ENTITY_RECORD_REQUEST } from "../actions";
import { createEntityItem, createEntityItemByIdErrorHandler } from "../entityItemsService";
import { getListRecordAfterCreateActions } from "../utils";
import history from '../../../../constants/History';

const request: Request<any, { item: any, entity: EntityName }> = {
  type: CREATE_ENTITY_RECORD_REQUEST,
  getData: ({ item, entity }) => createEntityItem(entity, item),
  retrieveData: (p, s) => processNotesAsyncQueue(s.actionsQueue.queuedActions),
  processData: (v, s, { entity }) => {
    history.push({
      pathname: history.location.pathname.replace('/new', ''),
      search:  history.location.search
    });
    return getListRecordAfterCreateActions(entity);
  },
  processError: (response, { item, entity }) => createEntityItemByIdErrorHandler(response, entity, item)
};

export const EpicCreateEntityRecord: Epic = Create(request);