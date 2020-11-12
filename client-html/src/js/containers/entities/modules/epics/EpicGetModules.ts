/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_MODULE_ITEMS, GET_MODULE_ITEMS_FULFILLED } from "../actions";
import { Module } from "@api/model";
import { getCustomColumnsMap } from "../../../../common/utils/common";

const defaultModuleMap = ({ id, values }) => ({
  id: Number(id),
  nationalCode: values[0],
  title: values[1]
});

const request: EpicUtils.Request<
  any,
  any,
  { offset?: number; columns?: string; ascending?: boolean; pageSize?: number }
> = {
  type: GET_MODULE_ITEMS,
  hideLoadIndicator: true,
  getData({ offset, columns, ascending, pageSize }, { modules: { search } }) {
    return EntityService.getPlainRecords(
      "Module",
      columns || "nationalCode,title",
      search,
      pageSize,
      offset,
      "",
      ascending
    );
  },
  processData({ rows, offset, pageSize }, s, { columns }) {
    const items: Module[] = rows.map(columns ? getCustomColumnsMap(columns) : defaultModuleMap);

    return [
      {
        type: GET_MODULE_ITEMS_FULFILLED,
        payload: { items, offset, pageSize }
      }
    ];
  }
};

export const EpicGetModules: Epic<any, any> = EpicUtils.Create(request);
