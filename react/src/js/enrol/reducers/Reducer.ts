import {combineReducers} from "redux";
import {CheckoutState, Phase} from "./State";
import * as L from "lodash";
import * as Actions from "../actions/Actions";
import * as ContactEditActions from "../containers/contact-edit/actions/Actions";
import * as ContactAddActions from "../containers/contact-add/actions/Actions";

import {ValidationError} from "../../model/common/ValidationError";
import {ContactFields} from "../../model/field/ContactFields";
import {Amount} from "../../model/checkout/Amount";
import {Reducer as SummaryReducer} from "../containers/summary/reducers/Reducer";
import {Reducer as PaymentReducer} from "../containers/payment/reducers/Reducer";
import {Reducer as ConcessionReducer} from "../containers/concession/reducers/Reducer";
import {ContactsSchema, ContactsState} from "../../NormalizeSchema";
import {RedeemVoucher} from "../../model/checkout/RedeemVoucher";
import {IAction} from "../../actions/IshAction";
import {normalize} from "normalizr";
import {FULFILLED} from "../../common/actions/ActionUtils";
import log = Handlebars.log;

const PageReducer = (state: Phase = Phase.Summary, action: IAction<Phase>): Phase => {
    switch (action.type) {

      case Actions.CHANGE_PHASE:
        switch (action.payload) {
          case Phase.Summary:
          case Phase.Payment:
          case Phase.Result:
            return action.payload;
          default:
            return state;
        }

      default:
        return state;
    }
};

const PayerReducer = (state: string = null, action: IAction<string>): string => {
  switch (action.type) {

    case Actions.SET_PAYER_TO_STATE:
      return action.payload;

    case Actions.RESET_CHECKOUT_STATE:
      return null;

    default:
      return state;
  }
};

const NewContactReducer = (state: boolean = false, action: IAction<boolean>): boolean => {
  switch (action.type) {

    case Actions.SET_NEW_CONTACT_FLAG:
      return action.payload;

    case ContactEditActions.SET_FIELDS_TO_STATE:
    case Actions.RESET_CHECKOUT_STATE:
      return false;

    default:
      return state;
  }
};

const FieldsReducer = (state: ContactFields = null, action: IAction<ContactFields>): ContactFields => {
  switch (action.type) {

    case ContactEditActions.SET_FIELDS_TO_STATE:
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

const ContactsReducer = (state: ContactsState = normalize([], ContactsSchema), action: IAction<ContactsState>): ContactsState => {
  let ns: ContactsState;

  switch (action.type) {
    case ContactAddActions.ADD_CONTACT_TO_STATE:

      ns = L.cloneDeep(state);
      ns.entities.contact = {...ns.entities.contact, ...action.payload.entities.contact};
      ns.result = Array.from(new Set([...ns.result, ...action.payload.result]));
      return ns;

    case Actions.UPDATE_PARENT_CHILDS:
      ns = L.cloneDeep(state);
      ns.result.map(id => ns.entities.contact[id].parentRequired = false);
      return ns;

    case Actions.RESET_CHECKOUT_STATE:
      return null;

    default:
      return state;
  }
};

const AmountReducer = (state: Amount = null, action: IAction<Amount>): Amount => {
  switch (action.type) {

    case Actions.UPDATE_AMOUNT:
      return action.payload;

    case Actions.RESET_CHECKOUT_STATE:
      return null;

    default:
      return state;
  }
};

const ErrorReducer = (state: ValidationError = null, action: IAction<ValidationError>): ValidationError => {
  switch (action.type) {

    case Actions.SHOW_MESSAGES:
      return action.payload;

    case Actions.CHANGE_PHASE:
    case ContactAddActions.ADD_CONTACT_TO_STATE:
    case ContactEditActions.SET_FIELDS_TO_STATE:
    case ContactEditActions.RESET_FIELDS_STATE:
    case Actions.UPDATE_AMOUNT:
      return null;
    default:
      return state;
  }
};

const PhaseReducer = (state: Phase = Phase.Init, action: IAction<Phase>): Phase => {
  switch (action.type) {

    case Actions.CHANGE_PHASE:
      return action.payload;

    default:
      return state;
  }
};

const RedeemVouchersReducer = (state: RedeemVoucher[] = [], action: IAction<any>): RedeemVoucher[] => {
  switch (action.type) {

    case FULFILLED(Actions.ADD_REDEEM_VOUCHER_TO_STATE):
      const voucher = action.payload;
      return state.filter(v => v.id === voucher.id).length ? state : state.concat(voucher);

    case Actions.SET_REDEEM_VOUCHER_ACTIVITY:
      const {id, enabled} = action.payload;
      return state.map(v => v.id === id ? {...v, enabled} : v);

    default:
      return state;
  }
};

const ContactAddProcess = (state: any = {}, action: IAction<any>): any => {
  switch (action.type) {

    case Actions.UPDATE_CONTACT_ADD_PROCESS:
      const {contact, type, parent} = action.payload;
      return {...state, contact, type, parent};

    default:
      return state;
  }
};

export const Reducer = combineReducers<CheckoutState>({
  newContact: NewContactReducer,
  fields: FieldsReducer,
  phase: PhaseReducer,
  page: PageReducer,
  error: ErrorReducer,
  amount: AmountReducer,
  summary: SummaryReducer,
  payment: PaymentReducer,
  payerId: PayerReducer,
  contacts: ContactsReducer,
  concession: ConcessionReducer,
  redeemVouchers: RedeemVouchersReducer,
  contactAddProcess: ContactAddProcess,
});
