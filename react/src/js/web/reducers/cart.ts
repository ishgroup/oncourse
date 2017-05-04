import {combineReducers} from "redux";
import {IshAction} from "../../actions/IshAction";
import {CourseClassCartState, ProductCartState} from "../../services/IshState";
import {Contact} from "../../model/web/Contact";
import {FULFILLED} from "../../common/actions/ActionUtils";
import {Actions} from "../actions/Actions";

function classesAllIds(state = [], action: IshAction<CourseClassCartState>) {
  switch (action.type) {
    case FULFILLED(Actions.ADD_CLASS_TO_CART):
      return [
        ...state,
        ...[action.payload.result]
          .filter(t => !state.includes(t)) // dedup
      ];
    case FULFILLED(Actions.REMOVE_CLASS_FROM_CART):
      return state.filter(it => it !== action.payload.result);
    default:
      return state;
  }
}

function classesById(state = {}, action: IshAction<CourseClassCartState>) {
  switch (action.type) {
    case FULFILLED(Actions.ADD_CLASS_TO_CART):
      return {
        ...state,
        ...action.payload.entities.classes
      };
    case FULFILLED(Actions.REMOVE_CLASS_FROM_CART):
      const nextState = {...state};
      delete nextState[action.payload.result];
      return nextState;
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
          .filter(t => !state.includes(t)) // dedup
      ];
    case FULFILLED(Actions.REMOVE_PRODUCT_FROM_CART):
      return state.filter(it => it !== action.payload.result);
    default:
      return state;
  }
}

function productsById(state = {}, action: IshAction<ProductCartState>) {
  switch (action.type) {
    case FULFILLED(Actions.ADD_PRODUCT_TO_CART):
      return {
        ...state,
        ...action.payload.entities.products
      };
    case FULFILLED(Actions.REMOVE_PRODUCT_FROM_CART):
      const nextState = {...state};
      delete nextState[action.payload.result];
      return nextState;
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
          .filter(t => !state.includes(t)) // dedup
      ];
    case FULFILLED(Actions.REMOVE_PROMOTION_FROM_CART):
      return state.filter(it => it !== action.payload.result);
    default:
      return state;
  }
}

function promotionsById(state = {}, action: IshAction<ProductCartState>) {
  switch (action.type) {
    case FULFILLED(Actions.ADD_PROMOTION_TO_CART):
      return {
        ...state,
        ...action.payload.entities.promotions
      };
    case FULFILLED(Actions.REMOVE_PROMOTION_FROM_CART):
      const nextState = {...state};
      delete nextState[action.payload.result];
      return nextState;
    default:
      return state;
  }
}

function contactReducer(state = {}, action: IshAction<Contact>) {
  switch (action.type) {
    case FULFILLED(Actions.REQUEST_CONTACT):
      return action.payload;
    default:
      return state;
  }
}

export const cartReducer = combineReducers({
  courses: combineReducers({
    entities: classesById,
    result: classesAllIds
  }),
  products: combineReducers({
    entities: productsById,
    result: productsAllIds
  }),
  promotions: combineReducers({
    entities: promotionsById,
    result: promotionsAllIds
  }),
  contact: contactReducer
});
