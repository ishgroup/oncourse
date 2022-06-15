/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import {
  GET_RECORDS_REQUEST,
  GET_RECORDS_FULFILLED
} from "../../js/common/components/list-view/actions";
import { EpicGetEntities } from "../../js/common/components/list-view/epics/EpicGetEntities";
import { DefaultEpic } from "./Default.Epic";

export const GetEntities = (entity: string, records: []) => DefaultEpic({
  action: {
    type: GET_RECORDS_REQUEST,
    payload: { entity, viewAll: false }
  },
  epic: EpicGetEntities,
  processData: () => [
    {
      type: GET_RECORDS_FULFILLED,
      payload: {
        records,
        payload: { entity, viewAll: false },
        searchQuery: {
          filter: "",
          offset: 0,
          pageSize: 50,
          search: "",
          tagGroups: [],
          uncheckedChecklists: []
        }
      }
    }
  ]
});
