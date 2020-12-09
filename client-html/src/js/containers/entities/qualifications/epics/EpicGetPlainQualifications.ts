/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { Qualification, DataResponse } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_PLAIN_QUALIFICATION_ITEMS, GET_PLAIN_QUALIFICATION_ITEMS_FULFILLED } from "../actions";
import { State } from "../../../../reducers/state";

const request: EpicUtils.Request<DataResponse, State, { offset?: number; sortings?: string; ascending?: boolean, pageSize?: number }> = {
  type: GET_PLAIN_QUALIFICATION_ITEMS,
  hideLoadIndicator: true,
  getData: ({
 offset, sortings, ascending, pageSize
}, { qualification: { search } }) =>
    EntityService.getPlainRecords(
      "Qualification",
      "nationalCode,title,level,fieldOfEducation,isOffered",
      search,
      pageSize || 100,
      offset,
      sortings,
      ascending
    ),
  processData: ({ rows, offset, pageSize }) => {
    const items: Qualification[] = rows.map(({ id, values }) => ({
      id: Number(id),
      nationalCode: values[0],
      title: values[1],
      qualLevel: values[2],
      fieldOfEducation: values[3],
      isOffered: JSON.parse(values[4])
    }));

    return [
      {
        type: GET_PLAIN_QUALIFICATION_ITEMS_FULFILLED,
        payload: { items, offset, pageSize }
      }
    ];
  }
};

export const EpicGetPlainQualifications: Epic<any, any> = EpicUtils.Create(request);
