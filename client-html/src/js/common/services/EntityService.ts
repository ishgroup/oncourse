/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { DataResponse, EntityApi, SearchQuery } from "@api/model";
import { DefaultHttpService } from "./HttpService";
import {
 LIST_PAGE_SIZE, PLAIN_LIST_MAX_PAGE_SIZE, SIMPLE_SEARCH_QUOTES_REGEX, SIMPLE_SEARCH_REGEX
} from "../../constants/Config";
import { State } from "../../reducers/state";
import { getFiltersString, getTagGroups } from "../components/list-view/utils/listFiltersUtils";
import { GetRecordsArgs } from "../../model/common/ListView";

class EntityService {
  readonly entityApi = new EntityApi(new DefaultHttpService());

  public getList(payload: GetRecordsArgs, state?: State): Promise<any> {
    const {
      viewAll, listUpdate, entity, stopIndex
    } = payload;

    let search = state ? state.list.search || "" : "";

    if (search.match(SIMPLE_SEARCH_QUOTES_REGEX)) {
      search = `~${search}`;
    } else if (search.match(SIMPLE_SEARCH_REGEX)) {
      search = `~"${search}"`;
    }

    let pageSize = LIST_PAGE_SIZE;
    let offset = 0;

    if (viewAll) {
      pageSize = PLAIN_LIST_MAX_PAGE_SIZE;
      offset = 0;
    } else if (listUpdate) {
      const recordsLength = state.list.records.rows.length;
      if (typeof stopIndex === "number" && stopIndex > 0) {
        pageSize = (stopIndex + 1) - recordsLength;
        if (pageSize < 0) pageSize = LIST_PAGE_SIZE;
        offset = recordsLength;
      } else {
        pageSize = recordsLength;
      }
    }

    const searchQuery: SearchQuery = {};
    searchQuery.search = search;
    searchQuery.pageSize = pageSize;
    searchQuery.offset = offset;
    searchQuery.filter = getFiltersString(state.list.filterGroups);
    searchQuery.tagGroups = getTagGroups([...state.list.menuTags, ...state.list.checkedChecklists]);
    searchQuery.uncheckedChecklists = getTagGroups(state.list.uncheckedChecklists);

    return this.entityApi.getAll(entity, searchQuery).then(res => [res, searchQuery]);
  }

  public updateTableModel(entity, model) {
    return this.entityApi.updateTableModel(entity, model);
  }

  public getRecords(entity: string, search: string): Promise<DataResponse> {
    return this.entityApi.get(entity, search, PLAIN_LIST_MAX_PAGE_SIZE, 0);
  }

  public getPlainRecords(
    entity: string,
    columns: string,
    search?: string,
    pageSize?: number,
    offset?: number,
    sortings?: string,
    ascending?: boolean
  ): Promise<DataResponse> {
    return this.entityApi.getPlain(entity, search, pageSize || PLAIN_LIST_MAX_PAGE_SIZE, offset || 0, columns, sortings, ascending);
  }
}

export default new EntityService();
