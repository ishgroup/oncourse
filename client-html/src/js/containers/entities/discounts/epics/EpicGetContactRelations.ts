/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { DataResponse } from "@api/model";
import { Epic } from "redux-observable";
import { NestedListPanelItem } from "../../../../common/components/form/nestedList/NestedList";
import { Create, Request } from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_DISCOUNT_CONTACT_RELATION_TYPES, GET_DISCOUNT_CONTACT_RELATION_TYPES_FULFILLED } from "../actions";

const request: Request = {
  type: GET_DISCOUNT_CONTACT_RELATION_TYPES,
  getData: () => EntityService.getPlainRecords("ContactRelationType", "toContactName", ""),
  processData: (response: DataResponse) => [
    {
      type: GET_DISCOUNT_CONTACT_RELATION_TYPES_FULFILLED,
      payload: {
        contactRelationTypes: response.rows
          .map(
            ({ id, values }) =>
              ({
                id: Number(id),
                description: values[0]
              } as NestedListPanelItem)
          )
          .sort((a, b) => (a.id < b.id ? -1 : a.id > b.id ? 1 : 0))
      }
    }
  ]
};

export const EpicGetContactRelations: Epic<any, any> = Create(request);
