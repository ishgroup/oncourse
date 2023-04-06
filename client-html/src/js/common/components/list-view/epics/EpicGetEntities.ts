/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { Request, Create } from "../../../epics/EpicUtils";
import EntityService from "../../../services/EntityService";
import {
  GET_RECORDS_FULFILLED,
  GET_RECORDS_FULFILLED_RESOLVE,
  GET_RECORDS_REQUEST,
  setListSearchError,
  setListSelection
} from "../actions";
import { State } from "../../../../reducers/state";
import { GetRecordsArgs } from "../../../../model/common/ListView";
import FetchErrorHandler from "../../../api/fetch-errors-handlers/FetchErrorHandler";

const request: Request<any, GetRecordsArgs> = {
  type: GET_RECORDS_REQUEST,
  getData: (payload, state) => EntityService.getList(payload, state),
  processData: ([records, searchQuery], state: State, payload) => {
    const {
     listUpdate, savedID, ignoreSelection, resolve
    } = payload;

    return [
      {
        type: GET_RECORDS_FULFILLED,
        payload: { records, payload, searchQuery }
      },
      ...(!ignoreSelection && !listUpdate && state.list.selection[0] !== "NEW"
        ? savedID && records.rows.find(r => String(r.id) === String(savedID))
          ? [setListSelection([String(savedID)])]
          : []
        : []),
      ...resolve ? [{
        type: GET_RECORDS_FULFILLED_RESOLVE,
        payload: { resolve }
      }] : [],
    ];
  },
  processError: response => {
    if (response && response.status === 400 && response.data.errorMessage.includes("Invalid search expression")) {
      return [setListSearchError(true)];
    }

    return FetchErrorHandler(response);
  }
};

export const EpicGetEntities: Epic<any, any> = Create(request);
