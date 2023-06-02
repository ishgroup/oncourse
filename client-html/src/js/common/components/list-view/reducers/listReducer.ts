/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { IAction } from "../../../actions/IshAction";
import {
  GET_EMAIL_TEMPLATES_WITH_KEYCODE_FULFILLED,
  GET_SCRIPTS_FULFILLED
} from "../../../actions";
import { LIST_PAGE_SIZE, LIST_SIDE_BAR_DEFAULT_WIDTH, PLAIN_LIST_MAX_PAGE_SIZE } from "../../../../constants/Config";
import {
  CLEAR_LIST_STATE,
  GET_FILTERS_FULFILLED,
  GET_RECORDS_FULFILLED,
  GET_RECORDS_REQUEST,
  SET_LIST_CORE_FILTERS,
  SET_LIST_EDIT_RECORD,
  SET_LIST_LAYOUT,
  SET_LIST_SAVING_FILTER,
  SET_LIST_SEARCH,
  SET_LIST_ENTITY,
  SET_LIST_SELECTION,
  SET_LIST_USER_AQL_SEARCH,
  GET_PLAIN_RECORDS_REQUEST_FULFILLED,
  SET_LIST_MENU_TAGS,
  SET_LIST_SEARCH_ERROR,
  SET_LIST_CREATING_NEW,
  SET_LIST_FULL_SCREEN_EDIT_VIEW,
  SET_RECIPIENTS_MESSAGE_DATA,
  CLEAR_RECIPIENTS_MESSAGE_DATA,
  SET_LIST_EDIT_RECORD_FETCHING,
  UPDATE_TAGS_ORDER, SET_LIST_CUSTOM_TABLE_MODEL,
} from "../actions";
import { latestActivityStorageHandler } from "../../../utils/storage";
import { GetRecordsArgs, ListState } from "../../../../model/common/ListView";
import { getUpdated } from "../utils/listFiltersUtils";

class State implements ListState {
  menuTags = [];

  checkedChecklists = [];

  uncheckedChecklists = [];

  menuTagsLoaded = false;

  filterGroups = [];

  filterGroupsLoaded = false;

  showColoredDots = false;

  records = {
    entity: "",
    columns: [],
    rows: [],
    sort: [],
    offset: 0, // default offset for first request,
    pageSize: LIST_PAGE_SIZE,
    search: null,
    layout: null,
    filteredCount: 0,
    filterColumnWidth: LIST_SIDE_BAR_DEFAULT_WIDTH,
    tagsOrder: []
  };

  plainRecords = {};

  search = "";

  searchQuery = {
    search: "",
    filter: "",
    tagGroups: []
  };

  searchError = false;

  editRecord = null;

  recepients = null;

  selection = [];

  fetching = false;

  editRecordFetching = false;

  creatingNew = false;

  fullScreenEditView = false;

  customTableModel = null;
}

export const listReducer = (state: State = new State(), action: IAction<any>): any => {
  switch (action.type) {
    case GET_RECORDS_REQUEST:
      return {
        ...state,
        fetching: true
      };

    case GET_RECORDS_FULFILLED: {
      const { records, payload, searchQuery } = action.payload;
      const { stopIndex }: GetRecordsArgs = payload;

      let newRecords = state.records;
      newRecords = records;

      newRecords.rows = typeof stopIndex === "number"
        ? state.records.rows.concat(records.rows)
        : records.rows;

      if (newRecords.pageSize > LIST_PAGE_SIZE) {
        newRecords.pageSize = LIST_PAGE_SIZE;
      }

      return {
        ...state,
        records: {
          ...newRecords,
          sort: newRecords.sort.map(s => ({ ...s })),
          columns: newRecords.columns.map(c => ({ ...c })),
          rows: newRecords.rows.map(r => ({ ...r })),
          tagsOrder: [...newRecords.tagsOrder],
          filteredCount: newRecords.entity === "Audit" ? PLAIN_LIST_MAX_PAGE_SIZE : newRecords.filteredCount,
          filterColumnWidth: newRecords.filterColumnWidth < LIST_SIDE_BAR_DEFAULT_WIDTH
            ? LIST_SIDE_BAR_DEFAULT_WIDTH
            : newRecords.filterColumnWidth
        },
        searchQuery,
        fetching: false
      };
    }

    case GET_PLAIN_RECORDS_REQUEST_FULFILLED: {
      const { plainRecords } = action.payload;

      return {
        ...state,
        plainRecords,
        fetching: false
      };
    }

    case SET_LIST_EDIT_RECORD: {
      const { editRecord, name } = action.payload;

      if (editRecord && editRecord.id) {
        latestActivityStorageHandler(
          { name, date: new Date().toISOString(), id: editRecord.id },
          state.records.entity
        );
      }

      return {
        ...state,
        editRecordFetching: false,
        editRecord
      };
    }

    case SET_LIST_EDIT_RECORD_FETCHING: {
      return {
        ...state,
        editRecordFetching: true
      };
    }

    case SET_LIST_CREATING_NEW: {
      return {
        ...state,
        creatingNew: action.payload
      };
    }

    case SET_LIST_FULL_SCREEN_EDIT_VIEW: {
      return {
        ...state,
        fullScreenEditView: action.payload
      };
    }

    case GET_FILTERS_FULFILLED: {
      const { filterGroups } = action.payload;

      state.records.offset = 0;

      return {
        ...state,
        filterGroupsLoaded: true,
        filterGroups
      };
    }

    case SET_LIST_CORE_FILTERS: {
      const { filterGroups } = action.payload;

      state.records.offset = 0;

      return {
        ...state,
        filterGroups
      };
    }

    case SET_RECIPIENTS_MESSAGE_DATA: {
      return {
        ...state,
        recepients: {
          ...state.recepients,
          ...action.payload
        }
      };
    }

    case CLEAR_RECIPIENTS_MESSAGE_DATA: {
      return {
        ...state,
        recepients: {}
      };
    }

    case GET_EMAIL_TEMPLATES_WITH_KEYCODE_FULFILLED: {
      const emailTemplatesWithKeyCode = action.payload;
      return {
        ...state,
        emailTemplatesWithKeyCode
      };
    }

    case GET_SCRIPTS_FULFILLED: {
      const scripts = action.payload;

      return {
        ...state,
        scripts
      };
    }

    case SET_LIST_SEARCH_ERROR: {
      const { searchError } = action.payload;

      return {
        ...state,
        searchError
      };
    }

    case SET_LIST_SEARCH: {
      const { search } = action.payload;

      state.records.offset = 0;

      return {
        ...state,
        fetching: true,
        search
      };
    }

    case SET_LIST_ENTITY: {
      return {
        ...state,
        records: {
          ...state.records,
          entity: action.payload
        }
      };
    }

    case SET_LIST_CUSTOM_TABLE_MODEL: {
      return {
        ...state,
        customTableModel: action.payload
      };
    }

    case SET_LIST_USER_AQL_SEARCH: {
      const { userAQLSearch } = action.payload;
      return {
        ...state,
        userAQLSearch
      };
    }

    case SET_LIST_SELECTION: {
      const { selection } = action.payload;
      return {
        ...state,
        selection
      };
    }

    case SET_LIST_LAYOUT: {
      return {
        ...state,
        records: {
          ...state.records,
          layout: action.payload
        }
      };
    }

    case SET_LIST_SAVING_FILTER: {
      return {
        ...state,
        savingFilter: action.payload
      };
    }

    case SET_LIST_MENU_TAGS: {
      const { menuTags, checkedChecklists, uncheckedChecklists } = action.payload;

      state.records.offset = 0;

      return {
        ...state,
        menuTagsLoaded: true,
        menuTags: getUpdated(menuTags, null, null, null),
        checkedChecklists: getUpdated(checkedChecklists, null, null, null),
        uncheckedChecklists: getUpdated(uncheckedChecklists, null, null, null),
      };
    }

    case CLEAR_LIST_STATE: {
      return new State();
    }

    case UPDATE_TAGS_ORDER: {
      return {
        ...state,
        records: {
          ...state.records,
          tagsOrder: action.payload,
        },
      };
    }

    default:
      return state;
  }
};
