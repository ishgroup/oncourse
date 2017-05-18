import {combineReducers} from "redux";
import {CheckoutState, PayerState, Phase} from "./State";
import * as Actions from "../actions/Actions";
import * as ContactEditActions from "../containers/contact-edit/actions/Actions";
import * as ContactAddActions from "../containers/contact-add/actions/Actions";
import {ValidationError} from "../../model/common/ValidationError";
import {ContactFields} from "../../model/field/ContactFields";

const FieldsReducer = (state: ContactFields = null, action): any => {
  switch (action.type) {
    case ContactEditActions.FieldsLoad:
      return action.payload;
    case Actions.PhaseChange:
      return null;
    default:
      return state;
  }
};

const ContactReducer = (state = {}, action): any => {
  switch (action.type) {
    case ContactAddActions.ContactAdd:
      return action.payload.entities.contacts[action.payload.result];
    default:
      return state;
  }
};

const ErrorReducer = (state: ValidationError = null, action: any): any => {
  switch (action.type) {
    case Actions.MessagesSet:
      return action.payload;
    case Actions.Init:
    case Actions.PhaseChange:
    case ContactAddActions.ContactAdd:
    case ContactEditActions.FieldsLoad:
    case ContactEditActions.FieldsSave:
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



