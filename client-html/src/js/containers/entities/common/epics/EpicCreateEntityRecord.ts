/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { Create, Request } from "../../../../common/epics/EpicUtils";
import { getRecords, setListSelection } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { EntityName } from "../../../../model/entities/common";
import { createEntityItem, updateEntityItemByIdErrorHandler } from "../entityItemsService";
import { FETCH_SUCCESS } from "../../../../common/actions";
import { CREATE_ENTITY_RECORD_REQUEST } from "../actions";

const request: Request<any, { item: any, entity: EntityName }> = {
  type: CREATE_ENTITY_RECORD_REQUEST,
  getData: ({ item, entity }) => createEntityItem(entity, item),
  processData: (v, s, { entity }) => [
      {
        type: FETCH_SUCCESS,
        payload: { message: `${entity} record created` }
      },
      getRecords(entity, true),
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ],
  processError: (response, { item, entity }) => updateEntityItemByIdErrorHandler(response, entity, item)
};

export const EpicCreateEntityRecord: Epic = Create(request);
