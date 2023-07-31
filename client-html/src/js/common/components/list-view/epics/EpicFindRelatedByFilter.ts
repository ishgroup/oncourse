/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../epics/EpicUtils";
import { FIND_RELATED_BY_FILTER } from "../actions";
import { DataResponse } from "@api/model";
import EntityService from "../../../services/EntityService";
import { openInternalLink } from "ish-ui";

const request: EpicUtils.Request<DataResponse, { filter: string | Function, list: string }> = {
  type: FIND_RELATED_BY_FILTER,
  getData: (p, {
    list: {
      searchQuery,
      records: {entity}
    }
  }) => EntityService.getRecordsByListSearch(entity as any, searchQuery),
  processData: (data, s, {filter, list}) => {
    const rowIds = data.rows.map(r => r.id).toString();

    const search = typeof filter === "function"
      ? filter(rowIds)
      : `${filter} in (${rowIds})`;

    openInternalLink(`/${list}?search=${search}`);
    return [];
  }
};

export const EpicFindRelatedByFilter: Epic<any, any> = EpicUtils.Create(request);