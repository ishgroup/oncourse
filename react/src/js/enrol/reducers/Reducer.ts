import {combineReducers} from "redux";
import {EnrolState, PayerState, Phase} from "./State";
import * as Actions from "../actions/Actions";
import {ValidationError} from "../../model/common/ValidationError";

const contactReducer = (state = {}, action): any => {
  switch (action.type) {
    case Actions.ContactAdd:
      return action.payload.entities.contacts[action.payload.result];
    default:
      return state;
  }
};

const errorReducer = (state: ValidationError = null, action: any): any => {
  switch (action.type) {
    case Actions.InitReject:
    case Actions.ContactAddReject:
      return action.payload;
    case Actions.ContactAdd:
    case Actions.Init:
    case Actions.PhaseChange:
      return {};
    default:
      return state;
  }
};

const phaseReducer = (state: Phase = Phase.Init, action: any): any => {
  switch (action.type) {
    case Actions.PhaseChange:
      return action.payload;
    default:
      return state;
  }
};


export const Reducer = combineReducers<EnrolState>({
  phase: phaseReducer,
  error: errorReducer,
  payer: combineReducers<PayerState>({
    entity: contactReducer,
  })
});



