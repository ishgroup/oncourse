import {combineReducers} from "redux";
import {EnrolState, PayerState} from "./State";
import * as Actions from "../actions/Actions";
import {ValidationError} from "../../model/common/ValidationError";
import extend = hbs.Utils.extend;

const contactReducer = (state = {}, action): any => {
  switch (action.type) {
    case Actions.ContactAdd:
      return action.payload.entities.contacts[action.payload.result];
    default:
      return state;
  }
};

const errorReducer = (state:ValidationError = null, action: any): any => {
  switch (action.type) {
    case Actions.ContactAddReject:
      return action.payload;
    case Actions.ContactAdd:
      return {};
    default:
      return state;
  }
};


export const Reducer = combineReducers<EnrolState>({
  error: errorReducer,
  payer: combineReducers<PayerState>({
    entity: contactReducer,
  })
});



