/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_PLAIN_QUALIFICATION_ITEMS, GET_PLAIN_QUALIFICATION_ITEMS_FULFILLED } from "../actions/index";
import { Qualification, DataResponse } from "@api/model";
import { State } from "../../../../reducers/state";

const request: EpicUtils.Request<DataResponse, State, { offset?: number; sortings?: string; ascending?: boolean }> = {
  type: GET_PLAIN_QUALIFICATION_ITEMS,
  hideLoadIndicator: true,
  getData: ({ offset, sortings, ascending }, { qualification: { search } }) =>
    EntityService.getPlainRecords(
      "Qualification",
      "nationalCode,title,level,fieldOfEducation",
      search ? `~"${search}"` : null,
      100,
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
      fieldOfEducation: values[3]
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
