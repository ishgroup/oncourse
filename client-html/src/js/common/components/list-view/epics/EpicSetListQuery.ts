/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import { ListQueryPayload } from '../../../../model/common/ListView';
import FetchErrorHandler from '../../../api/fetch-errors-handlers/FetchErrorHandler';

import * as EpicUtils from '../../../epics/EpicUtils';
import EntityService from '../../../services/EntityService';
import { GET_RECORDS_FULFILLED, SET_LIST_QUERY, setListSearchError } from '../actions';

/**
 * The query is already in the store by the time this runs, so the request is always built from
 * one consistent snapshot of search text, filters and tags.
 */
const request: EpicUtils.Request<any, ListQueryPayload> = {
  type: SET_LIST_QUERY,
  getData: ({ entity }, state) => EntityService.getList({ entity }, state),
  processData: ([records, searchQuery], state, payload) => [
    {
      type: GET_RECORDS_FULFILLED,
      payload: { records, payload, searchQuery }
    },
    ...(state.list.searchError ? [setListSearchError(false)] : [])
  ],
  processError: response => {
    if (response && response.status === 400 && response.data.errorMessage.includes("Invalid search expression")) {
      return [setListSearchError(true)];
    }

    return FetchErrorHandler(response);
  }
};

export const EpicSetListQuery: Epic<any, any> = EpicUtils.Create(request);
