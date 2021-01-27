import {combineReducers} from "redux";
import {IshAction} from "../../actions/IshAction";
import {
  ContactState,
  CourseClassCartState,
  ProductCartState,
  ReplaceCourseClassState,
  WaitingCourseClassState
} from "../../services/IshState";
import {Contact} from "../../model";
import {FULFILLED} from "../../common/actions/ActionUtils";
import {Actions} from "../actions/Actions";
import {RESET_CHECKOUT_STATE} from "../../enrol/actions/Actions";

function classesAllIds(state = [], action: IshAction<CourseClassCartState & ReplaceCourseClassState>) {
  switch (action.type) {
    case FULFILLED(Actions.ADD_CLASS_TO_CART):
      return [
        ...state,
        ...[action.payload.result]
          .filter(t => !state.includes(t)),
      ];

    case FULFILLED(Actions.REPLACE_CLASS_IN_CART):
      return [
        ...state,
        ...[action.payload.replacement.result]
          .filter(t => !state.includes(t))
      ].filter(it => it !== action.payload.replace.result);

    case FULFILLED(Actions.REMOVE_CLASS_FROM_CART):
      return state.filter(it => it !== action.payload.result);

    case RESET_CHECKOUT_STATE:
      return [];

    default:
      return state;
  }
}

function classesById(state = {}, action: IshAction<CourseClassCartState & ReplaceCourseClassState>) {
  switch (action.type) {
    case FULFILLED(Actions.ADD_CLASS_TO_CART):
      return {
        ...state,
        ...action.payload.entities.classes,
      };

    case FULFILLED(Actions.REPLACE_CLASS_IN_CART):
      const replaceState = {...state, ...action.payload.replacement.entities.classes};
      delete replaceState[action.payload.replace.result];

      return replaceState;

    case FULFILLED(Actions.REMOVE_CLASS_FROM_CART):
      const nextState = {...state};
      delete nextState[action.payload.result];
      return nextState;

    case RESET_CHECKOUT_STATE:
      return {};

    default:
      return state;
  }
}

function productsAllIds(state = [], action: IshAction<ProductCartState>) {
  switch (action.type) {
    case FULFILLED(Actions.ADD_PRODUCT_TO_CART):
      return [
        ...state,
        ...[action.payload.result]
          .filter(t => !state.includes(t)), // dedup
      ];

    case FULFILLED(Actions.REMOVE_PRODUCT_FROM_CART):
      return state.filter(it => it !== action.payload.result);

    case RESET_CHECKOUT_STATE:
      return [];

    default:
      return state;
  }
}

function productsById(state = {}, action: IshAction<ProductCartState>) {
  switch (action.type) {
    case FULFILLED(Actions.ADD_PRODUCT_TO_CART):
      return {
        ...state,
        ...action.payload.entities.products,
      };

    case FULFILLED(Actions.REMOVE_PRODUCT_FROM_CART):
      const nextState = {...state};
      delete nextState[action.payload.result];
      return nextState;

    case RESET_CHECKOUT_STATE:
      return {};

    default:
      return state;
  }
}

function promotionsAllIds(state = [], action: IshAction<ProductCartState>) {
  switch (action.type) {
    case FULFILLED(Actions.ADD_PROMOTION_TO_CART):
      return [
        ...state,
        ...[action.payload.result]
          .filter(t => !state.includes(t)), // dedup
      ];

    case FULFILLED(Actions.REMOVE_PROMOTION_FROM_CART):
      return state.filter(it => it !== action.payload.result);

    case RESET_CHECKOUT_STATE:
      return [];

    default:
      return state;
  }
}

function promotionsById(state = {}, action: IshAction<ProductCartState>) {
  switch (action.type) {
    case FULFILLED(Actions.ADD_PROMOTION_TO_CART):
      return {
        ...state,
        ...action.payload.entities.promotions,
      };
    case FULFILLED(Actions.REMOVE_PROMOTION_FROM_CART):
      const nextState = {...state};
      delete nextState[action.payload.result];
      return nextState;

    case RESET_CHECKOUT_STATE:
      return {};

    default:
      return state;
  }
}

function waitingCourseAllIds(state = [], action: IshAction<WaitingCourseClassState>) {
  switch (action.type) {
    case FULFILLED(Actions.ADD_WAITING_COURSE_TO_CART):
      return [
        ...state,
        ...[action.payload.result]
          .filter(t => !state.includes(t)), // dedup
      ];

    case FULFILLED(Actions.REMOVE_WAITING_COURSE_FROM_CART):
      return state.filter(it => it !== action.payload.result);

    case RESET_CHECKOUT_STATE:
      return [];

    default:
      return state;
  }
}

function waitingCoursesById(state = {}, action: IshAction<WaitingCourseClassState>) {
  switch (action.type) {
    case FULFILLED(Actions.ADD_WAITING_COURSE_TO_CART):
      return {
        ...state,
        ...action.payload.entities.waitingCourses,
      };

    case FULFILLED(Actions.REMOVE_WAITING_COURSE_FROM_CART):
      const nextState = {...state};
      delete nextState[action.payload.result];
      return nextState;

    case RESET_CHECKOUT_STATE:
      return {};

    default:
      return state;
  }
}

function contactReducer(state:ContactState = {}, action: IshAction<Contact>) {
  switch (action.type) {
    case FULFILLED(Actions.REQUEST_CONTACT):
      return action.payload;

    case RESET_CHECKOUT_STATE:
      return {};

    default:
      return state;
  }
}

export const cartReducer = combineReducers({
  courses: combineReducers({
    entities: classesById,
    result: classesAllIds,
  }),
  products: combineReducers({
    entities: productsById,
    result: productsAllIds,
  }),
  promotions: combineReducers({
    entities: promotionsById,
    result: promotionsAllIds,
  }),
  waitingCourses: combineReducers({
    entities: waitingCoursesById,
    result: waitingCourseAllIds,
  }),
  contact: contactReducer,
});
