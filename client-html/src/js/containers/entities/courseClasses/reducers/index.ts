/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import {
 ClassCost, EnrolmentStatus, Session, SessionWarning
} from "@api/model";
import { IAction } from "../../../../common/actions/IshAction";
import {
  CLEAR_DUPLICATE_COURSE_CLASSES_SESSIONS,
  SET_COURSE_CLASS_ENROLMENTS,
  GET_DUPLICATE_COURSE_CLASSES_SESSIONS,
  SET_DUPLICATE_COURSE_CLASSES_SESSIONS,
  GET_COURSE_CLASS_ENROLMENTS,
  GET_PLAIN_COURSE_CLASSES,
  GET_PLAIN_COURSE_CLASSES_FULFILLED,
  SET_PLAIN_COURSE_CLASSES_SEARCH,
  DUPLICATE_COURSE_CLASS,
  DUPLICATE_COURSE_CLASS_FULFILLED,
  CLEAR_PLAIN_COURSE_CLASSES_SEARCH,
  SET_COURSE_CLASS_LATEST_SESSION,
  SET_COURSE_CLASS_BUDGET_MODAL_OPENED,
  GET_PLAIN_COURSE_CLASSES_BY_ID_FULFILLED,
  GET_PLAIN_COURSE_CLASSES_BY_ID,
  SET_COURSE_CLASS_SESSIONS_WARNINGS, CLEAR_PLAIN_COURSE_CLASSES, SET_DUPLICATE_COURSE_CLASSES_BUDGET
} from "../actions/index";
import { CoruseClassesSate, CourseClassBulkSession } from "./state";
import { StringKeyAndValueObject } from "../../../../model/common/CommomObjects";
import { SET_COURSE_CLASS_TUTOR_NAMES_WARNINGS } from "../components/tutors/actions";
import {
  COURSE_CLASS_CLOSE_BULK_UPDATE_MODAL,
  COURSE_CLASS_OPEN_BULK_UPDATE_MODAL,
  COURSE_CLASS_SELECT_BULK_SESSION,
  COURSE_CLASS_SELECT_SINGLE_SESSION
} from "../components/timetable/actions";

export interface CourseClassState {
  timetable: {
    sessions: Session[];
    earliest: Date;
    hasZeroWages: boolean;
    fetching: boolean;
  };
  sessionWarnings?: SessionWarning[];
  enrolments?: { id: string; createdOn: string; status: EnrolmentStatus }[];
  enrolmentsFetching?: boolean;
  tutorNamesWarnings?: StringKeyAndValueObject;
  latestSession?: Date;
  budgetModalOpened?: boolean;
  defaultOnCostRate?: number;
  duplicateTraineeshipBudget?: ClassCost[];
}

const initial: CourseClassState = {
  timetable: {
    sessions: [],
    earliest: null,
    hasZeroWages: false,
    fetching: false
  },
  sessionWarnings: [],
  enrolments: [],
  enrolmentsFetching: true,
  tutorNamesWarnings: {},
  latestSession: null,
  budgetModalOpened: false,
  duplicateTraineeshipBudget: []
};

export const courseClassReducer = (state: CourseClassState = initial, action: IAction<any>): any => {
  switch (action.type) {
    case SET_DUPLICATE_COURSE_CLASSES_BUDGET: {
      return {
        ...state,
        duplicateTraineeshipBudget: action.payload
      };
    }

    case GET_COURSE_CLASS_ENROLMENTS: {
      return {
        ...state,
        enrolmentsFetching: true
      };
    }

    case SET_COURSE_CLASS_ENROLMENTS: {
      return {
        ...state,
        ...action.payload,
        enrolmentsFetching: false
      };
    }

    case DUPLICATE_COURSE_CLASS:
    case GET_DUPLICATE_COURSE_CLASSES_SESSIONS: {
      return {
        ...state,
        timetable: {
          ...state.timetable,
          fetching: true
        }
      };
    }

    case DUPLICATE_COURSE_CLASS_FULFILLED: {
      return {
        ...state,
        timetable: {
          ...state.timetable,
          fetching: false
        }
      };
    }

    case SET_DUPLICATE_COURSE_CLASSES_SESSIONS: {
      return {
        ...state,
        timetable: {
          ...state.timetable,
          ...action.payload,
          fetching: false
        }
      };
    }

    case CLEAR_DUPLICATE_COURSE_CLASSES_SESSIONS: {
      return {
        ...state,
        timetable: {
          sessions: [],
          earliest: null,
          fetching: false
        }
      };
    }

    case SET_COURSE_CLASS_SESSIONS_WARNINGS:
    case SET_COURSE_CLASS_TUTOR_NAMES_WARNINGS:
    case SET_COURSE_CLASS_LATEST_SESSION: {
      return {
        ...state,
        ...action.payload
      };
    }

    case SET_COURSE_CLASS_BUDGET_MODAL_OPENED: {
      const { opened, onCostRate } = action.payload;

      return {
        ...state,
        budgetModalOpened: opened,
        defaultOnCostRate: opened ? onCostRate : null
      };
    }

    default:
      return state;
  }
};

const courseClassesInitial: CoruseClassesSate = {
  items: [],
  search: "",
  loading: false,
  rowsCount: 5000,
  checkEmpty: false
};

export const courseClassesReducer = (state: CoruseClassesSate = courseClassesInitial, action: IAction<any>): any => {
  switch (action.type) {
    case GET_PLAIN_COURSE_CLASSES: {
      return {
        ...state,
        loading: true
      };
    }

    case GET_PLAIN_COURSE_CLASSES_BY_ID: {
      return {
        ...state,
        loading: true
      };
    }

    case CLEAR_PLAIN_COURSE_CLASSES: {
      return {
        ...state,
        loading: false,
        items: []
      };
    }

    case GET_PLAIN_COURSE_CLASSES_FULFILLED: {
      const { items, offset, pageSize } = action.payload;
      const updated = offset ? state.items.concat(items) : items;

      return {
        ...state,
        loading: false,
        items: updated,
        rowsCount: pageSize < 100 ? pageSize : 5000
      };
    }

    case GET_PLAIN_COURSE_CLASSES_BY_ID_FULFILLED: {
      const { items, offset, pageSize } = action.payload;
      const updated = offset ? state.items.concat(items) : items;

      return {
        ...state,
        loading: false,
        items: updated,
        rowsCount: pageSize < 100 ? pageSize : 5000,
        checkEmpty: updated.length <= 0
      };
    }

    case CLEAR_PLAIN_COURSE_CLASSES_SEARCH:
    case SET_PLAIN_COURSE_CLASSES_SEARCH: {
      return {
        ...state,
        ...action.payload
      };
    }

    default:
      return state;
  }
};

const courseClassBulkSessionInitial: CourseClassBulkSession = {
  selection: [],
  modalOpened: false,
  onUpdate: () => {},
  tutors: []
};

export const courseClassesBulkSessionReducer = (
  state: CourseClassBulkSession = courseClassBulkSessionInitial,
  action: IAction<any>
): any => {
  switch (action.type) {
    case COURSE_CLASS_SELECT_BULK_SESSION: {
      return {
        ...state,
        selection: action.payload.sessions
      };
    }

    case COURSE_CLASS_SELECT_SINGLE_SESSION: {
      const updatedSession = [...state.selection];
      const { id } = action.payload.session;

      if (updatedSession.includes(id)) {
        const index = updatedSession.findIndex(s => s === id);
        updatedSession.splice(index, 1);
      } else {
        updatedSession.push(id);
      }

      return {
        ...state,
        selection: updatedSession
      };
    }

    case COURSE_CLASS_OPEN_BULK_UPDATE_MODAL: {
      const { onUpdate, tutors } = action.payload;
      return {
        ...state,
        modalOpened: true,
        onUpdate,
        tutors
      };
    }

    case COURSE_CLASS_CLOSE_BULK_UPDATE_MODAL: {
      return {
        ...state,
        modalOpened: false,
        onUpdate: () => {}
      };
    }

    default:
      return state;
  }
};
