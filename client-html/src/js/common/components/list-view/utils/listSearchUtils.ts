/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { ENTITY_AQL_STORAGE_NAME, FILTER_TAGS_REGEX } from '../../../../constants/Config';
import { FilterGroup, ListUrlQuery } from '../../../../model/common/ListView';
import { FindEntityState } from '../../../../model/entities/common';
import { FormMenuTag } from '../../../../model/tags';
import { saveCategoryAQLLink } from '../../../utils/links';
import { LSGetItem } from '../../../utils/storage';
import { getActiveTags, getFiltersNameString } from './listFiltersUtils';

/**
 * List view query params owned by the list view. Any other param found in the url
 * (deep links, one off flags) is preserved untouched when the query is written back.
 */
export const LIST_URL_QUERY_PARAMS = ["search", "filter", "tags", "checkedChecklists", "uncheckedChecklists"] as const;

/**
 * Params that are acted on once when the list opens and then have no meaning. They are stripped
 * from the url so that a reload or a copied link does not replay them.
 */
export const CONSUMED_URL_PARAMS = ["openShare", "customSearch"];

const EMPTY_QUERY: ListUrlQuery = {
  search: "",
  filter: "",
  tags: "",
  checkedChecklists: "",
  uncheckedChecklists: ""
};

/**
 * Percent encodes only the characters that would otherwise change the meaning of the query
 * string. Everything else (including `@`, `"`, spaces and commas) is left as typed so the
 * url stays readable, and `URLSearchParams` reads every value back exactly as it went in.
 *
 * `%` has to go first, `+` has to be escaped because `URLSearchParams` reads a literal `+`
 * back as a space.
 */
const encodeQueryPart = (value: string): string => value
  .replace(/%/g, "%25")
  .replace(/&/g, "%26")
  .replace(/=/g, "%3D")
  .replace(/#/g, "%23")
  .replace(/\+/g, "%2B");

const stringifyUrlSearch = (entries: [string, string][]): string => {
  const result = entries
    .map(([key, value]) => `${encodeQueryPart(key)}=${encodeQueryPart(value)}`)
    .join("&");

  return result ? `?${result}` : "";
};

const getActiveTagIdsString = (tags: FormMenuTag[] = []): string =>
  Array.from(new Set(getActiveTags(tags).map(t => t.tagBody.id))).toString();

export const parseListUrlSearch = (urlSearch: string | URLSearchParams): ListUrlQuery => {
  const params = typeof urlSearch === "string" ? new URLSearchParams(urlSearch) : urlSearch;

  return LIST_URL_QUERY_PARAMS.reduce((query, param) => {
    query[param] = params.get(param) || "";
    return query;
  }, { ...EMPTY_QUERY });
};

/**
 * The single projection of the list state onto the url. `search` holds the text the user
 * actually typed (`userAQLSearch`), never the expanded expression, so that reopening the link
 * puts the very same text back into the search field.
 */
export const getListUrlQuery = (list: {
  userAQLSearch?: string;
  filterGroups?: FilterGroup[];
  menuTags?: FormMenuTag[];
  checkedChecklists?: FormMenuTag[];
  uncheckedChecklists?: FormMenuTag[];
}): ListUrlQuery => ({
  search: list.userAQLSearch || "",
  filter: getFiltersNameString(list.filterGroups || []),
  tags: getActiveTagIdsString(list.menuTags),
  checkedChecklists: getActiveTagIdsString(list.checkedChecklists),
  uncheckedChecklists: getActiveTagIdsString(list.uncheckedChecklists)
});

export const isSameListUrlQuery = (a: ListUrlQuery, b: ListUrlQuery): boolean =>
  LIST_URL_QUERY_PARAMS.every(param => (a[param] || "") === (b[param] || ""));

export const hasListUrlQuery = (urlSearch: string | URLSearchParams): boolean => {
  const params = typeof urlSearch === "string" ? new URLSearchParams(urlSearch) : urlSearch;
  return LIST_URL_QUERY_PARAMS.some(param => params.has(param));
};

/**
 * Writes the list query onto the current url search string, keeping every foreign param
 * in place and in its original position.
 */
export const buildListUrlSearch = (currentUrlSearch: string, query: ListUrlQuery): string => {
  const current = new URLSearchParams(currentUrlSearch);
  const entries: [string, string][] = [];
  const written = new Set<string>();

  current.forEach((value, key) => {
    if (CONSUMED_URL_PARAMS.includes(key)) {
      return;
    }

    if ((LIST_URL_QUERY_PARAMS as readonly string[]).includes(key)) {
      if (written.has(key)) {
        return;
      }
      written.add(key);
      if (query[key]) {
        entries.push([key, query[key]]);
      }
      return;
    }
    entries.push([key, value]);
  });

  LIST_URL_QUERY_PARAMS.forEach(param => {
    if (!written.has(param) && query[param]) {
      entries.push([param, query[param]]);
    }
  });

  return stringifyUrlSearch(entries);
};

export const removeUrlParams = (currentUrlSearch: string, remove: string[]): string => {
  const entries: [string, string][] = [];

  new URLSearchParams(currentUrlSearch).forEach((value, key) => {
    if (!remove.includes(key)) {
      entries.push([key, value]);
    }
  });

  return stringifyUrlSearch(entries);
};

export const parseTagIds = (idsString: string): number[] => (idsString
  ? idsString.split(",").map(id => Number(id)).filter(id => !Number.isNaN(id))
  : []);

/**
 * `customSearch` links carry an id of an AQL expression kept in local storage by
 * `saveCategoryAQLLink`. The entry is consumed on read, so the param has to be swapped for the
 * expression it resolves to and dropped from the url - otherwise a reload would search for the
 * bare id.
 *
 * Mutates the passed params: removes `customSearch`. Returns the resolved AQL, an empty string
 * when the link was already spent, or null when there was no `customSearch` param at all.
 */
export const resolveCustomSearch = (params: URLSearchParams): string => {
  if (!params.has("customSearch")) {
    return null;
  }

  const id = params.get("customSearch");
  params.delete("customSearch");

  const storedState = LSGetItem(ENTITY_AQL_STORAGE_NAME);
  const entityState: FindEntityState = storedState ? JSON.parse(storedState as string) : null;
  const stored = entityState?.data?.find(item => item.id === id);

  if (!stored) {
    // already consumed - fall back to whatever `search` the url carries, never to the bare id
    return params.get("search") || "";
  }

  saveCategoryAQLLink({ AQL: "", id, action: "remove" });

  return stored.AQL;
};

/**
 * Replaces every `@Filter_Name` reference in the search text with the filter expression it
 * stands for, so the AQL sent to the server is complete.
 *
 * Pure by design: filter and tag activation stays with the sidebar, which is the only place
 * that can change it without breaking the boolean structure of the typed expression
 * (`@A or name is "x"` must not also be ANDed with `A` by the filter param).
 */
export const expandAqlSearch = (value: string, filterGroups: FilterGroup[] = []): string => {
  if (!value) {
    return "";
  }

  const filters = filterGroups.flatMap(group => group.filters);

  return value.replace(FILTER_TAGS_REGEX, str => {
    const name = str.replace(/@/g, "").replace(/_/g, " ");
    const filter = filters.find(f => f.name === name);

    return filter ? `(${filter.expression})` : "";
  });
};
