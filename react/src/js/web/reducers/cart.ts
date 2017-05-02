import {combineReducers} from "redux";
import {IshAction} from "../../actions/IshAction";
import {FULFILLED, IshActions} from "../../constants/IshActions";
import {CourseClassCartState, ProductCartState} from "../../services/IshState";
import {Contact} from "../../model/web/Contact";

function classesAllIds(state = [], action: IshAction<CourseClassCartState>) {
  switch (action.type) {
    case FULFILLED(IshActions.ADD_CLASS_TO_CART):
      return [
        ...state,
        ...[action.payload.result]
          .filter(t => !state.includes(t)) // dedup
      ];
    case FULFILLED(IshActions.REMOVE_CLASS_FROM_CART):
      return state.filter(it => it !== action.payload.result);
    default:
      return state;
  }
}

function classesById(state = {}, action: IshAction<CourseClassCartState>) {
  switch (action.type) {
    case FULFILLED(IshActions.ADD_CLASS_TO_CART):
      return {
        ...state,
        ...action.payload.entities.classes
      };
    case FULFILLED(IshActions.REMOVE_CLASS_FROM_CART):
      const nextState = {...state};
      delete nextState[action.payload.result];
      return nextState;
    default:
      return state;
  }
}

function productsAllIds(state = [], action: IshAction<ProductCartState>) {
  switch (action.type) {
    case FULFILLED(IshActions.ADD_PRODUCT_TO_CART):
      return [
        ...state,
        ...[action.payload.result]
          .filter(t => !state.includes(t)) // dedup
      ];
    case FULFILLED(IshActions.REMOVE_PRODUCT_FROM_CART):
      return state.filter(it => it !== action.payload.result);
    default:
      return state;
  }
}

function productsById(state = {}, action: IshAction<ProductCartState>) {
  switch (action.type) {
    case FULFILLED(IshActions.ADD_PRODUCT_TO_CART):
      return {
        ...state,
        ...action.payload.entities.products
      };
    case FULFILLED(IshActions.REMOVE_PRODUCT_FROM_CART):
      const nextState = {...state};
      delete nextState[action.payload.result];
      return nextState;
    default:
      return state;
  }
}

function promotionsAllIds(state = [], action: IshAction<ProductCartState>) {
  switch (action.type) {
    case FULFILLED(IshActions.ADD_PROMOTION_TO_CART):
      return [
        ...state,
        ...[action.payload.result]
          .filter(t => !state.includes(t)) // dedup
      ];
    case FULFILLED(IshActions.REMOVE_PROMOTION_FROM_CART):
      return state.filter(it => it !== action.payload.result);
    default:
      return state;
  }
}

function promotionsById(state = {}, action: IshAction<ProductCartState>) {
  switch (action.type) {
    case FULFILLED(IshActions.ADD_PROMOTION_TO_CART):
      return {
        ...state,
        ...action.payload.entities.promotions
      };
    case FULFILLED(IshActions.REMOVE_PROMOTION_FROM_CART):
      const nextState = {...state};
      delete nextState[action.payload.result];
      return nextState;
    default:
      return state;
  }
}

function contactReducer(state = {}, action: IshAction<Contact>) {
  switch (action.type) {
    case FULFILLED(IshActions.REQUEST_CONTACT):
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
