/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from "../../../../common/actions/IshAction";
import {
  GET_PLAIN_COURSES,
  SET_PLAIN_COURSES,
  SET_PLAIN_COURSES_SEARCH,
  GET_COURSE_FULFILLED,
  CLEAR_PLAIN_COURSES_SEARCH
} from "../actions";
import { CoursesState } from "./state";

const initial: CoursesState = {
  items: [],
  search: "",
  enrolmentTypeSearch: null,
  loading: false,
  rowsCount: 5000
};

export const coursesReducer = (state: CoursesState = initial, action: IAction<any>): any => {
  switch (action.type) {
    case GET_PLAIN_COURSES: {
      return {
        ...state,
        loading: true
      };
    }

    case SET_PLAIN_COURSES: {
      const { items, offset, pageSize, loading } = action.payload;

      const updated = offset ? state.items.concat(items) : items;

      return {
        ...state,
        loading,
        items: updated,
        rowsCount: pageSize < 100 ? pageSize : 5000
      };
    }

    case SET_PLAIN_COURSES_SEARCH: {
      return {
        ...state,
        ...action.payload
      };
    }

    case CLEAR_PLAIN_COURSES_SEARCH: {
      return {
        ...state,
        ...action.payload
      };
    }

    case GET_COURSE_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }

    default:
      return state;
  }
};
