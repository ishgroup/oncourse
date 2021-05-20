/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Diff, Filter, LayoutType, MessageType, SearchQuery, TableModel } from "@api/model";
import { MessageData } from "../../../../model/common/Message";
import { _toRequestType, FULFILLED } from "../../../actions/ActionUtils";
import { MenuTag } from "../../../../model/tags";
import { ApiMethods } from "../../../../model/common/apiHandlers";
import { GetRecordsArgs, SavingFilterState } from "../../../../model/common/ListView";
import { AnyArgFunction } from "../../../../model/common/CommonFunctions";
import { IAction } from "../../../actions/IshAction";
import { EntityName } from "../../../../model/entities/common";

export const GET_LIST_NESTED_EDIT_RECORD = _toRequestType("get/listView/nestedEditRecord");

export const UPDATE_LIST_NESTED_EDIT_RECORD = _toRequestType("post/listView/nestedEditRecord");

export const GET_RECORDS_REQUEST = _toRequestType("get/records");
export const GET_RECORDS_FULFILLED = FULFILLED(GET_RECORDS_REQUEST);

export const GET_PLAIN_RECORDS_REQUEST = _toRequestType("get/records/plain");
export const GET_PLAIN_RECORDS_REQUEST_FULFILLED = FULFILLED(GET_PLAIN_RECORDS_REQUEST);

export const GET_FILTERS_REQUEST = _toRequestType("get/filter");
export const GET_FILTERS_FULFILLED = FULFILLED(GET_FILTERS_REQUEST);

export const GET_RECIPIENTS_MESSAGE_DATA = _toRequestType("get/recipients");
export const SET_RECIPIENTS_MESSAGE_DATA = "set/recipients";
export const CLEAR_RECIPIENTS_MESSAGE_DATA = "clear/recipients";

export const DELETE_FILTER_REQUEST = _toRequestType("delete/filter");

export const POST_FILTER_REQUEST = _toRequestType("post/filter");
export const POST_FILTER_REQUEST_FULFILLED = FULFILLED(POST_FILTER_REQUEST);

export const UPDATE_TABLE_MODEL_REQUEST = _toRequestType("update/table/model");

export const BULK_CHANGE_RECORDS = _toRequestType("post/listView/bulkChange");

export const CLEAR_LIST_STATE = "clear/listView";

export const SET_LIST_CORE_FILTERS = "set/listView/coreFilters";

export const SET_LIST_SEARCH = "set/listView/search";

export const SET_LIST_ENTITY = "set/listView/entity";

export const SET_LIST_COLUMNS = "set/listView/columns";

export const SET_LIST_SEARCH_ERROR = "set/listView/search/error";

export const SET_LIST_SELECTION = "set/listView/selection";

export const SET_LIST_SAVING_FILTER = "set/listView/savingFilter";

export const SET_LIST_LAYOUT = "set/listView/layout";

export const SET_LIST_USER_AQL_SEARCH = "set/listView/userAQLSearch";

export const SET_LIST_MENU_TAGS = "set/listView/menuTags";

export const SET_LIST_EDIT_RECORD = "set/listView/editRecord";

export const SET_LIST_CREATING_NEW = "set/listView/creatingNew";

export const SET_LIST_FULL_SCREEN_EDIT_VIEW = "set/listView/fullScreenEditView";

export const SET_LIST_NESTED_EDIT_RECORD = "set/listView/nestedEditRecord";

export const SET_LIST_EDIT_RECORD_FETCHING = "set/listView/editRecordFetching";

export const CLOSE_LIST_NESTED_EDIT_RECORD = "close/listView/nestedEditRecord";

export const CLEAR_LIST_NESTED_EDIT_RECORD = "clear/listView/nestedEditRecord";

export const deleteCustomFilter = (id: number, entity: string, checked: boolean) => ({
  type: DELETE_FILTER_REQUEST,
  payload: { id, entity, checked }
});

export const createCustomFilter = (filter: Filter, entity: string) => ({
  type: POST_FILTER_REQUEST,
  payload: { filter, entity }
});

export const setListUserAQLSearch = (userAQLSearch: string) => ({
  type: SET_LIST_USER_AQL_SEARCH,
  payload: { userAQLSearch }
});

export const setListLayout = (layout: LayoutType) => ({
  type: SET_LIST_LAYOUT,
  payload: layout
});

export const setListMenuTags = (menuTags: MenuTag[]) => ({
  type: SET_LIST_MENU_TAGS,
  payload: { menuTags }
});

export const getRecords = (
  entity: string,
  listUpdate?: boolean,
  viewAll?: boolean,
  startIndex?: number,
  stopIndex?: number,
  resolve?: AnyArgFunction
): IAction<GetRecordsArgs> => ({
  type: GET_RECORDS_REQUEST,
  payload: {
    entity, listUpdate, viewAll, startIndex, stopIndex, resolve
  }
});

export const getPlainRecords = (entity: string, columns: string) => ({
  type: GET_PLAIN_RECORDS_REQUEST,
  payload: { entity, columns }
});

export const getFilters = (entity: string) => ({
  type: GET_FILTERS_REQUEST,
  payload: { entity }
});

export const getRecipientsMessageData = (entityName: string, messageType: MessageType, searchQuery: SearchQuery, selection: string[]) => ({
  type: GET_RECIPIENTS_MESSAGE_DATA,
  payload: {
   entityName, messageType, searchQuery, selection
  }
});

export const setRecipientsMessageData = (recepients: MessageData) => ({
  type: SET_RECIPIENTS_MESSAGE_DATA,
  payload: recepients
});

export const clearRecipientsMessageData = () => ({
  type: CLEAR_RECIPIENTS_MESSAGE_DATA
});

export const clearListState = () => ({
  type: CLEAR_LIST_STATE
});

export const setListEditRecordFetching = () => ({
  type: SET_LIST_EDIT_RECORD_FETCHING
});

export const updateTableModel = (entity: string, model: TableModel, listUpdate?: boolean) => ({
  type: UPDATE_TABLE_MODEL_REQUEST,
  payload: { entity, model, listUpdate }
});

export const setListSavingFilter = (savingFilter?: SavingFilterState) => ({
  type: SET_LIST_SAVING_FILTER,
  payload: savingFilter
});

export const setListSelection = (selection: string[]) => ({
  type: SET_LIST_SELECTION,
  payload: { selection }
});

export const setFilterGroups = filterGroups => ({
  type: SET_LIST_CORE_FILTERS,
  payload: { filterGroups }
});

export const setSearch = (search: string, entity: string) => ({
  type: SET_LIST_SEARCH,
  payload: { search, entity }
});

export const setListColumns = columns => ({
  type: SET_LIST_COLUMNS,
  payload: { columns }
});

export const setListSearchError = (searchError: boolean) => ({
  type: SET_LIST_SEARCH_ERROR,
  payload: { searchError }
});

export const setListEditRecord = (editRecord: any) => ({
  type: SET_LIST_EDIT_RECORD,
  payload: { editRecord }
});

export const setListCreatingNew = (creatingNew: boolean) => ({
  type: SET_LIST_CREATING_NEW,
  payload: creatingNew
});

export const setListFullScreenEditView = (fullScreenEditView: boolean) => ({
  type: SET_LIST_FULL_SCREEN_EDIT_VIEW,
  payload: fullScreenEditView
});

export const getListNestedEditRecord = (entity: string, id: number, index?: number, threeColumn?: boolean) => ({
  type: GET_LIST_NESTED_EDIT_RECORD,
  payload: {
    entity, id, index, threeColumn
  }
});

export const setListNestedEditRecord = (entity: string, record: any, customOnSave: any, hideFullScreenAppBar?: boolean) => ({
  type: SET_LIST_NESTED_EDIT_RECORD,
  payload: {
    entity, record, opened: true, customOnSave, hideFullScreenAppBar
  }
});

export const clearListNestedEditRecord = (index: number) => ({
  type: CLEAR_LIST_NESTED_EDIT_RECORD,
  payload: { index }
});

export const closeListNestedEditRecord = (index: number) => ({
  type: CLOSE_LIST_NESTED_EDIT_RECORD,
  payload: { index }
});

export const updateListNestedEditRecord = (
  id: number,
  record: any,
  entity: string,
  index: number,
  listRootEntity: string,
  method?: ApiMethods
) => ({
  type: UPDATE_LIST_NESTED_EDIT_RECORD,
  payload: {
    entity, id, record, index, listRootEntity, method
  }
});

export const bulkChangeRecords = (entity: EntityName, diff: Diff) => ({
  type: BULK_CHANGE_RECORDS,
  payload: { entity, diff }
});

export const setListEntity = (entity: EntityName) => ({
  type: SET_LIST_ENTITY,
  payload: entity
});
