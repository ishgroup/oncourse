/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
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
          tagGroups: []
        }
      }
    }
  ]
});
