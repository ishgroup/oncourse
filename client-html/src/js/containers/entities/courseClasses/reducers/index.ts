/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ClassCost, EnrolmentStatus, Session, SessionWarning } from "@api/model";
import { StringKeyAndValueObject } from "ish-ui";
import { IAction } from "../../../../common/actions/IshAction";
import {
  CLEAR_DUPLICATE_COURSE_CLASSES_SESSIONS,
  DUPLICATE_COURSE_CLASS,
  DUPLICATE_COURSE_CLASS_FULFILLED,
  GET_COURSE_CLASS_ENROLMENTS,
  GET_DUPLICATE_COURSE_CLASSES_SESSIONS,
  SET_COURSE_CLASS_BUDGET_MODAL_OPENED,
  SET_COURSE_CLASS_ENROLMENTS,
  SET_COURSE_CLASS_LATEST_SESSION,
  SET_COURSE_CLASS_SESSIONS_WARNINGS,
  SET_DUPLICATE_COURSE_CLASSES_BUDGET,
  SET_DUPLICATE_COURSE_CLASSES_SESSIONS
} from "../actions";
import {
  COURSE_CLASS_CLOSE_BULK_UPDATE_MODAL,
  COURSE_CLASS_OPEN_BULK_UPDATE_MODAL,
  COURSE_CLASS_SELECT_BULK_SESSION,
  COURSE_CLASS_SELECT_SINGLE_SESSION
} from "../components/timetable/actions";
import { SET_COURSE_CLASS_TUTOR_NAMES_WARNINGS } from "../components/tutors/actions";
import { CourseClassBulkSession } from "./state";

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

const courseClassBulkSessionInitial: CourseClassBulkSession = {
  selection: [],
  modalOpened: false
};

export const courseClassesBulkSessionReducer = (
  state: CourseClassBulkSession = courseClassBulkSessionInitial,
  action: IAction<any>
): CourseClassBulkSession => {
  switch (action.type) {
    case COURSE_CLASS_SELECT_BULK_SESSION: {
      return {
        ...state,
        selection: action.payload.sessions
      };
    }

    case COURSE_CLASS_SELECT_SINGLE_SESSION: {
      const updatedSession = [...state.selection];
      const id = action.payload.session.id || action.payload.session.temporaryId;

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
      return {
        ...state,
        modalOpened: true
      };
    }

    case COURSE_CLASS_CLOSE_BULK_UPDATE_MODAL: {
      return {
        ...state,
        modalOpened: false
      };
    }

    default:
      return state;
  }
};
