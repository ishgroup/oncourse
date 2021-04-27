/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../epics/EpicUtils";
import CustomFiltersService from "../../../services/CustomFiltersService";
import { State } from "../../../../reducers/state";
import { GET_FILTERS_FULFILLED, GET_FILTERS_REQUEST, GET_RECORDS_REQUEST } from "../actions/index";
import { CoreFilter } from "../../../../model/common/ListView";

const request: EpicUtils.Request = {
  type: GET_FILTERS_REQUEST,
  getData: payload => CustomFiltersService.getFilters(payload.entity),
  processData: (filters: CoreFilter[], state: State, payload) => {
    filters.forEach(f => {
      f.active = false;
    });

    let filterGroups = state.list.filterGroups.map(f => ({ ...f }));

    const customFiltersIndex = state.list.filterGroups.findIndex(i => i.title === "Custom Filters");

    if (customFiltersIndex === -1) {
      filterGroups = [...filterGroups, ...(filters.length ? [{ title: "Custom Filters", filters }] : [])];
    } else {
      if (filters.length) {
        filterGroups[customFiltersIndex].filters = filters;
      } else {
        filterGroups.splice(customFiltersIndex, 1);
      }
    }

    return [
      {
        type: GET_FILTERS_FULFILLED,
        payload: {
          filterGroups,
          entity: payload.entity
        }
      },
      ...(payload.listUpdate
        ? [
            {
              type: GET_RECORDS_REQUEST,
              payload: {
                entity: payload.entity
              }
            }
          ]
        : [])
    ];
  }
};

export const EpicGetFilters: Epic<any, any> = EpicUtils.Create(request);
