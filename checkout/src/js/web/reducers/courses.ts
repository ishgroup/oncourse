import {CoursesState, InactiveCoursesState, WaitingCoursesState} from "../../services/IshState";
import {IshAction} from "../../actions/IshAction";
import {combineReducers} from "redux";
import {FULFILLED} from "../../common/actions/ActionUtils";
import {Actions} from "../actions/Actions";

function courseAllIds(state = [], action: IshAction<CoursesState>) {
  switch (action.type) {
    case FULFILLED(Actions.REQUEST_COURSE_CLASS):
    case Actions.PutClassToStore:
      return [
        ...state,
        ...action.payload.result
          .filter(t => !state.includes(t)), // dedup
      ];
    default:
      return state;
  }
}

function courseById(state = {}, action: IshAction<CoursesState>) {
  switch (action.type) {
    case FULFILLED(Actions.REQUEST_COURSE_CLASS):
    case Actions.PutClassToStore:
      return {
        ...state,
        ...action.payload.entities.classes,
      };
    default:
      return state;
  }
}

export const coursesReducer = combineReducers({
  entities: courseById,
  result: courseAllIds,
});

function waitingCourseAllIds(state = [], action: IshAction<WaitingCoursesState>) {
  switch (action.type) {
    case FULFILLED(Actions.REQUEST_WAITING_COURSE):
      return [
        ...state,
        ...action.payload.result
          .filter(t => !state.includes(t)),
      ];
    default:
      return state;
  }
}

function waitingCourseById(state = {}, action: IshAction<WaitingCoursesState>) {
  switch (action.type) {
    case FULFILLED(Actions.REQUEST_WAITING_COURSE):
      return {
        ...state,
        ...action.payload.entities.waitingCourses,
      };
    default:
      return state;
  }
}

export const waitingCoursesReducer = combineReducers({
  entities: waitingCourseById,
  result: waitingCourseAllIds,
});

function inactiveCourseAllIds(state = [], action: IshAction<InactiveCoursesState>) {
  switch (action.type) {
    case FULFILLED(Actions.REQUEST_INACTIVE_COURSE):
      return [
        ...state,
        ...action.payload.result
          .filter(t => !state.includes(t)),
      ];
    default:
      return state;
  }
}

function inactiveCourseById(state = {}, action: IshAction<InactiveCoursesState>) {
  switch (action.type) {
    case FULFILLED(Actions.REQUEST_INACTIVE_COURSE):
      return {
        ...state,
        ...action.payload.entities.inactiveCourses,
      };
    default:
      return state;
  }
}

export const inactiveCoursesReducer = combineReducers({
  entities: inactiveCourseById,
  result: inactiveCourseAllIds,
});


