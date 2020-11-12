/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_PLAIN_ROOMS, setPlainRooms } from "../actions/index";
import { DataResponse } from "@api/model";
import { getCustomColumnsMap, sortDefaultSelectItems } from "../../../../common/utils/common";
import { State } from "../../../../reducers/state";

const request: EpicUtils.Request<
  DataResponse,
  State,
  { offset?: number; columns?: string; ascending?: boolean; sort?: string; search?: string }
> = {
  type: GET_PLAIN_ROOMS,
  hideLoadIndicator: true,
  getData: ({ offset, columns, ascending, sort, search }, { sites }) => {
    return EntityService.getPlainRecords(
      "Room",
      columns || "name",
      search || sites.search || "",
      100,
      offset,
      sort,
      ascending
    );
  },
  processData: (response: DataResponse, s, { columns }) => {
    const { rows, offset, pageSize } = response;

    let items: any[];

    if (columns) {
      items = rows.map(getCustomColumnsMap(columns));
    } else {
      items = rows.map(({ id, values }) => ({
        value: Number(id),
        label: values[0]
      }));

      items.sort(sortDefaultSelectItems);
    }

    return [setPlainRooms(items, offset, pageSize)];
  }
};

export const EpicGetRooms: Epic<any, any> = EpicUtils.Create(request);
