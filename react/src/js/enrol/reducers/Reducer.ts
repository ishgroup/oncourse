import {combineReducers} from "redux";
import {CheckoutState, PayerState, Phase} from "./State";
import * as Actions from "../actions/Actions";
import {ValidationError} from "../../model/common/ValidationError";
import {ContactFields} from "../../model/field/ContactFields";

const FieldsReducer = (state:ContactFields = null, action): any => {
  switch (action.type) {
    case Actions.FieldsLoad:
      return action.payload;
    case Actions.PhaseChange:
      return null;
    default:
      return state;
  }
};

const ContactReducer = (state = {}, action): any => {
  switch (action.type) {
    case Actions.ContactAdd:
      return action.payload.entities.contacts[action.payload.result];
    default:
      return state;
  }
};

const ErrorReducer = (state: ValidationError = null, action: any): any => {
  switch (action.type) {
    case Actions.InitReject:
    case Actions.ContactAddReject:
    case Actions.FieldsLoadReject:
      return action.payload;
    case Actions.Init:
    case Actions.ContactAdd:
    case Actions.PhaseChange:
    case Actions.FieldsLoad:
      return {};
    default:
      return state;
  }
};

const PhaseReducer = (state: Phase = Phase.Init, action: any): any => {
  switch (action.type) {
    case Actions.PhaseChange:
      return action.payload;
    default:
      return state;
  }
};


export const Reducer = combineReducers<CheckoutState>({
  fields: FieldsReducer,
  phase: PhaseReducer,
  error: ErrorReducer,
  payer: combineReducers<PayerState>({
    entity: ContactReducer,
  })
});



