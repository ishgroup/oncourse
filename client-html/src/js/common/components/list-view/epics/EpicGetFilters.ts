/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import { CoreFilter } from '../../../../model/common/ListView';
import { State } from '../../../../reducers/state';

import * as EpicUtils from '../../../epics/EpicUtils';
import CustomFiltersService from '../../../services/CustomFiltersService';
import { GET_FILTERS_FULFILLED, GET_FILTERS_REQUEST, GET_RECORDS_REQUEST } from '../actions';

const request: EpicUtils.Request = {
  type: GET_FILTERS_REQUEST,
  getData: payload => CustomFiltersService.getFilters(payload.entity),
  processData: (filters: CoreFilter[], state: State, payload) => {
    const customFiltersIndex = state.list.filterGroups.findIndex(i => i.title === "Custom Filters");

    // a reload triggered by saving or deleting another filter must not silently drop the
    // selection the list is currently filtered by
    const previous = customFiltersIndex === -1 ? [] : state.list.filterGroups[customFiltersIndex].filters;

    filters.forEach(f => {
      f.active = previous.some(p => p.id === f.id && p.active);
    });

    let filterGroups = state.list.filterGroups.map(f => ({ ...f }));

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
