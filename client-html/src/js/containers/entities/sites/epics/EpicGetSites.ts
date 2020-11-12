/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_SITES_REQUEST, setPlainSites } from "../actions/index";
import { DataResponse } from "@api/model";
import { getCustomColumnsMap, sortDefaultSelectItems } from "../../../../common/utils/common";
import { State } from "../../../../reducers/state";

const request: EpicUtils.Request<
  DataResponse,
  State,
  { offset?: number; columns?: string; ascending?: boolean; sort?: string; search?: string; pageSize?: number }
> = {
  type: GET_SITES_REQUEST,
  hideLoadIndicator: true,
  getData: ({ offset, columns, ascending, sort, search, pageSize }, { sites }) => {
    return EntityService.getPlainRecords(
      "Site",
      columns || "name",
      search || sites.search || "",
      pageSize,
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

    return [setPlainSites(items, offset, pageSize)];
  }
};

export const EpicGetSites: Epic<any, any> = EpicUtils.Create(request);
