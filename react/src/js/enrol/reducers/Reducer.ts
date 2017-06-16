import {combineReducers} from "redux";
import {CheckoutState, PayerState, Phase} from "./State";
import * as Actions from "../actions/Actions";
import * as ContactEditActions from "../containers/contact-edit/actions/Actions";
import * as ContactAddActions from "../containers/contact-add/actions/Actions";

import {ValidationError} from "../../model/common/ValidationError";
import {ContactFields} from "../../model/field/ContactFields";
import {Amount} from "../../model/checkout/Amount";
import {Reducer as SummaryReducer} from "../containers/summary/reducers/Reducer";
import {Reducer as PaymentReducer} from "../containers/payment/reducers/Reducer";
import {ContactBox} from "../../NormalizeSchema";
import {Contact} from "../../model/web/Contact";
import {Promotion} from "../../model/web/Promotion";

const NewContactReducer = (state: boolean = false, action: { type: string, payload: boolean }): boolean => {
  switch (action.type) {
    case Actions.SET_NEW_CONTACT_FLAG:
      return action.payload;
    case ContactEditActions.FieldsSave:
    case Actions.RESET_CHECKOUT_STATE:
      return false;
    default:
      return state;
  }
};

const FieldsReducer = (state: ContactFields = null, action): any => {
  switch (action.type) {
    case ContactEditActions.FieldsLoad:
      return action.payload;
    case Actions.CHANGE_PHASE:
      switch (action.payload) {
        case Phase.EditContact:
          return state;
        default:
          return null;
      }
    case Actions.RESET_CHECKOUT_STATE:
      return null;
    default:
      return state;
  }
};

const ContactReducer = (state: Contact = null, action: { type: string, payload: ContactBox }): Contact => {
  switch (action.type) {
    case ContactAddActions.ADD_CONTACT:
      return action.payload.entities.contact[action.payload.result];
    case Actions.RESET_CHECKOUT_STATE:
      return null;
    default:
      return state;
  }
};


const AmountReducer = (state: Amount = null, action: { type: string, payload: Amount }): Amount => {
  switch (action.type) {
    case Actions.UPDATE_AMOUNT:
      return action.payload;
    case Actions.RESET_CHECKOUT_STATE:
      return null;
    default:
      return state;
  }
};

const ErrorReducer = (state: ValidationError = null, action: any): any => {
  switch (action.type) {
    case Actions.SHOW_MESSAGES:
      return action.payload;
    case Actions.CHANGE_PHASE:
    case ContactAddActions.ADD_CONTACT:
    case ContactEditActions.FieldsLoad:
    case ContactEditActions.FieldsSave:
      return {};
    default:
      return state;
  }
};

const PhaseReducer = (state: Phase = Phase.Init, action: any): any => {
  switch (action.type) {
    case Actions.CHANGE_PHASE:
      return action.payload;
    default:
      return state;
  }
};

const AddCodeReducer = (state: Promotion = null, action: { type: string, payload: Promotion }): any => {
  switch (action.type) {
    case Actions.ADD_CODE:
      return action.payload;
    default:
      return state;
  }
};

export const Reducer = combineReducers<CheckoutState>({
  newContact: NewContactReducer,
  fields: FieldsReducer,
  phase: PhaseReducer,
  addCode:AddCodeReducer,
  error: ErrorReducer,
  amount: AmountReducer,
  summary: SummaryReducer,
  payment: PaymentReducer,
  payer: combineReducers<PayerState>({
    entity: ContactReducer,
  }),
});

