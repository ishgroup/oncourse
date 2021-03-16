/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../epics/EpicUtils";
import EntityService from "../../../services/EntityService";
import { GET_RECORDS_FULFILLED, GET_RECORDS_REQUEST, setListSelection } from "../actions/index";
import { State } from "../../../../reducers/state";
import { GetRecordsArgs } from "../../../../model/common/ListView";

const request: EpicUtils.Request<any, GetRecordsArgs> = {
  type: GET_RECORDS_REQUEST,
  getData: (payload, state) => EntityService.getList(payload, state),
  processData: ([records, searchQuery], state: State, payload) => {
    const {
     listUpdate, savedID, ignoreSelection, resolve
    } = payload;

    if (resolve) {
      resolve();
    }

    return [
      {
        type: GET_RECORDS_FULFILLED,
        payload: { records, payload, searchQuery }
      },
      ...(!ignoreSelection && !listUpdate && state.list.selection[0] !== "NEW"
        ? savedID && records.rows.find(r => String(r.id) === String(savedID))
          ? [setListSelection([String(savedID)])]
          : []
        : [])
    ];
  }
};

export const EpicGetEntities: Epic<any, any> = EpicUtils.Create(request);
