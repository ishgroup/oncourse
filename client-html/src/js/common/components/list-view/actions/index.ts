/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Diff, Filter, LayoutType, MessageType, SearchQuery, TableModel } from '@api/model';
import { AnyArgFunction } from 'ish-ui';
import { GetRecordsArgs, SavingFilterState } from '../../../../model/common/ListView';
import { MessageData } from '../../../../model/common/Message';
import { CustomTableModelName, EntityName } from '../../../../model/entities/common';
import { FormMenuTag } from '../../../../model/tags';
import { _toRequestType, FULFILLED } from '../../../actions/ActionUtils';
import { IAction } from '../../../actions/IshAction';

// Common list actions
export const GET_RECORDS_REQUEST = _toRequestType("get/records");
export const GET_RECORDS_FULFILLED = FULFILLED(GET_RECORDS_REQUEST);
export const GET_RECORDS_FULFILLED_RESOLVE = FULFILLED(GET_RECORDS_REQUEST) + "_resolve";

export const GET_PLAIN_RECORDS_REQUEST = _toRequestType("get/records/plain");
export const GET_PLAIN_RECORDS_REQUEST_FULFILLED = FULFILLED(GET_PLAIN_RECORDS_REQUEST);

export const GET_FILTERS_REQUEST = _toRequestType("get/filter");
export const GET_FILTERS_FULFILLED = FULFILLED(GET_FILTERS_REQUEST);

export const GET_RECIPIENTS_MESSAGE_DATA = _toRequestType("get/recipients");
export const SET_RECIPIENTS_MESSAGE_DATA = "set/recipients";
export const CLEAR_RECIPIENTS_MESSAGE_DATA = "clear/recipients";
export const SET_READ_NEWS_LOCAL = "SET_READ_NEWS_LOCAL";

export const DELETE_FILTER_REQUEST = _toRequestType("delete/filter");

export const POST_FILTER_REQUEST = _toRequestType("post/filter");
export const POST_FILTER_REQUEST_FULFILLED = FULFILLED(POST_FILTER_REQUEST);

export const UPDATE_TABLE_MODEL_REQUEST = _toRequestType("update/table/model");

export const BULK_CHANGE_RECORDS = _toRequestType("post/listView/bulkChange");

export const FIND_RELATED_BY_FILTER = "find/related/byFilter";

export const SET_LIST_CORE_FILTERS = "set/listView/coreFilters";

export const SET_LIST_SEARCH = "set/listView/search";

export const SET_LIST_ENTITY = "set/listView/entity";

export const SET_LIST_CUSTOM_TABLE_MODEL = "set/listView/customTableModel";

export const SET_LIST_SEARCH_ERROR = "set/listView/search/error";

export const SET_LIST_SELECTION = "set/listView/selection";

export const SET_LIST_SAVING_FILTER = "set/listView/savingFilter";

export const SET_LIST_LAYOUT = "set/listView/layout";

export const SET_LIST_USER_AQL_SEARCH = "set/listView/userAQLSearch";

export const SET_LIST_MENU_TAGS = "set/listView/menuTags";

export const SET_LIST_EDIT_RECORD = "set/listView/editRecord";

export const UPDATE_TAGS_ORDER = "set/listView/tagsOrder";

export const SET_LIST_CREATING_NEW = "set/listView/creatingNew";

export const SET_LIST_FULL_SCREEN_EDIT_VIEW = "set/listView/fullScreenEditView";

export const SET_LIST_EDIT_RECORD_FETCHING = "set/listView/editRecordFetching";

export const findRelatedByFilter = (filter: string | AnyArgFunction<string, string>, list: string) => ({
  type: FIND_RELATED_BY_FILTER,
  payload: {filter, list}
});

export const deleteCustomFilter = (id: number, entity: string, checked: boolean) => ({
  type: DELETE_FILTER_REQUEST,
  payload: {id, entity, checked}
});

export const createCustomFilter = (filter: Filter, entity: string) => ({
  type: POST_FILTER_REQUEST,
  payload: {filter, entity}
});

export const setListUserAQLSearch = (userAQLSearch: string) => ({
  type: SET_LIST_USER_AQL_SEARCH,
  payload: {userAQLSearch}
});

export const setListLayout = (layout: LayoutType) => ({
  type: SET_LIST_LAYOUT,
  payload: layout
});

export const setListMenuTags = (menuTags: FormMenuTag[], checkedChecklists: FormMenuTag[], uncheckedChecklists: FormMenuTag[]) => ({
  type: SET_LIST_MENU_TAGS,
  payload: {menuTags, checkedChecklists, uncheckedChecklists}
});

export const getRecords = (
  payload: GetRecordsArgs
): IAction<GetRecordsArgs> => ({
  type: GET_RECORDS_REQUEST,
  payload
});

export const getPlainRecords = (entity: string, columns: string) => ({
  type: GET_PLAIN_RECORDS_REQUEST,
  payload: {entity, columns}
});

export const getFilters = (entity: string) => ({
  type: GET_FILTERS_REQUEST,
  payload: {entity}
});

export const getRecipientsMessageData = (entityName: string, messageType: MessageType, searchQuery: SearchQuery, selection: string[], templateId: number) => ({
  type: GET_RECIPIENTS_MESSAGE_DATA,
  payload: {
    entityName, messageType, searchQuery, selection, templateId
  }
});

export const setRecipientsMessageData = (recepients: MessageData) => ({
  type: SET_RECIPIENTS_MESSAGE_DATA,
  payload: recepients
});

export const clearRecipientsMessageData = () => ({
  type: CLEAR_RECIPIENTS_MESSAGE_DATA
});

export const setListEditRecordFetching = () => ({
  type: SET_LIST_EDIT_RECORD_FETCHING
});

export const updateTableModel = (entity: string, model: TableModel, listUpdate?: boolean) => ({
  type: UPDATE_TABLE_MODEL_REQUEST,
  payload: {entity, model, listUpdate}
});

export const setListSavingFilter = (savingFilter?: SavingFilterState) => ({
  type: SET_LIST_SAVING_FILTER,
  payload: savingFilter
});

export const setListSelection = (selection: string[]) => ({
  type: SET_LIST_SELECTION,
  payload: {selection}
});

export const setFilterGroups = filterGroups => ({
  type: SET_LIST_CORE_FILTERS,
  payload: {filterGroups}
});

export const setSearch = (search: string, entity: string) => ({
  type: SET_LIST_SEARCH,
  payload: {search, entity}
});

export const setListSearchError = (searchError: boolean) => ({
  type: SET_LIST_SEARCH_ERROR,
  payload: {searchError}
});

export const setListEditRecord = (editRecord: any) => ({
  type: SET_LIST_EDIT_RECORD,
  payload: {editRecord}
});

export const setListCreatingNew = (creatingNew: boolean) => ({
  type: SET_LIST_CREATING_NEW,
  payload: creatingNew
});

export const bulkChangeRecords = (entity: EntityName, diff: Diff) => ({
  type: BULK_CHANGE_RECORDS,
  payload: {entity, diff}
});

export const setListEntity = (entity: EntityName) => ({
  type: SET_LIST_ENTITY,
  payload: entity
});

export const setListCustomTableModel = (customTableModel: CustomTableModelName) => ({
  type: SET_LIST_CUSTOM_TABLE_MODEL,
  payload: customTableModel
});

export const setListFullScreenEditView = (fullScreenEditView: boolean) => ({
  type: SET_LIST_FULL_SCREEN_EDIT_VIEW,
  payload: fullScreenEditView
});

export const setReadNewsLocal = (id: string) => ({
  type: SET_READ_NEWS_LOCAL,
  payload: id,
});