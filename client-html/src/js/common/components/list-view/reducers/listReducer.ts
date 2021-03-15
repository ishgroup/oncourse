/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Category } from "@api/model";
import { IAction } from "../../../actions/IshAction";
import {
  GET_EMAIL_TEMPLATES_WITH_KEYCODE_FULFILLED,
  GET_SCRIPTS_FULFILLED
} from "../../../actions";
import { LIST_PAGE_SIZE } from "../../../../constants/Config";
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
  SET_LIST_NESTED_EDIT_RECORD,
  CLEAR_LIST_NESTED_EDIT_RECORD,
  CLOSE_LIST_NESTED_EDIT_RECORD,
  GET_PLAIN_RECORDS_REQUEST_FULFILLED,
  SET_LIST_MENU_TAGS,
  SET_LIST_SEARCH_ERROR,
  SET_LIST_CREATING_NEW,
  SET_LIST_FULL_SCREEN_EDIT_VIEW,
  SET_LIST_COLUMNS,
  SET_RECIPIENTS_MESSAGE_DATA,
  CLEAR_RECIPIENTS_MESSAGE_DATA,
  SET_LIST_EDIT_RECORD_FETCHING
} from "../actions";
import { latestActivityStorageHandler } from "../../../utils/storage";
import { GetRecordsArgs, ListState } from "../../../../model/common/ListView";
import { getUpdated } from "../utils/listFiltersUtils";

class State implements ListState {
  menuTags = [];

  menuTagsLoaded = false;

  filterGroups = [];

  filterGroupsLoaded = false;

  records = {
    entity: "",
    columns: [],
    rows: [],
    sort: [],
    offset: 0, // default offset for first request,
    pageSize: LIST_PAGE_SIZE,
    search: null,
    layout: null,
    filterColumnWidth: 200,
    recordsLeft: LIST_PAGE_SIZE
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

  nestedEditRecords = [];

  selection = [];

  fetching = false;

  editRecordFetching = false;

  creatingNew = false;

  fullScreenEditView = false;
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
      const { startIndex, stopIndex }: GetRecordsArgs = payload;

      let newRecords = state.records;
      newRecords = records;

      newRecords.rows = typeof startIndex === "number" && typeof stopIndex === "number"
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
          rows: newRecords.rows.map(r => ({ ...r }))
        },
        searchQuery,
        fetching: false,
        recordsLeft: records.pageSize
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
          state.records.entity as Category
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

    case SET_LIST_NESTED_EDIT_RECORD: {
      return {
        ...state,
        nestedEditRecords: [...state.nestedEditRecords, action.payload]
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

    case SET_LIST_COLUMNS: {
      const { columns } = action.payload;

      return {
        ...state,
        records: {
          ...state.records,
          columns
        }
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
      const { menuTags } = action.payload;

      state.records.offset = 0;

      return {
        ...state,
        menuTagsLoaded: true,
        menuTags: getUpdated(menuTags, null, null, null)
      };
    }

    case CLOSE_LIST_NESTED_EDIT_RECORD: {
      const { index } = action.payload;

      if (state.nestedEditRecords[index]) state.nestedEditRecords[index].opened = false;

      return {
        ...state,
        nestedEditRecords: [...state.nestedEditRecords]
      };
    }

    case CLEAR_LIST_NESTED_EDIT_RECORD: {
      const { index } = action.payload;

      state.nestedEditRecords.splice(index, 1);

      return {
        ...state,
        nestedEditRecords: [...state.nestedEditRecords]
      };
    }

    case CLEAR_LIST_STATE: {
      return new State();
    }

    default:
      return state;
  }
};
