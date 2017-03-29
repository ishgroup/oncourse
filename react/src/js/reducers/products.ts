import {CoursesState} from "../services/IshState";
import {IshAction} from "../actions/IshAction";
import {FULFILLED, IshActions} from "../constants/IshActions";
import {combineReducers} from "redux";

export const productsReducer = combineReducers({
  entities: byId,
  result: allIds
});

function allIds(state = [], action: IshAction<CoursesState>) {
  switch (action.type) {
    case FULFILLED(IshActions.REQUEST_PRODUCT):
      return [
        ...state,
        ...action.payload.result
          .filter(t => !state.includes(t)) // dedup
      ];
    default:
      return state;
  }
}

function byId(state = {}, action: IshAction<CoursesState>) {
  switch (action.type) {
    case FULFILLED(IshActions.REQUEST_PRODUCT):
      return {
        ...state,
        ...action.payload.entities.products
      };
    default:
      return state;
  }
}
