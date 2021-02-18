import {combineReducers} from "redux";
import {AmountState, CheckoutState, ContactFieldsState, Phase} from "./State";
import * as L from "lodash";
import * as Actions from "../actions/Actions";
import * as ContactEditActions from "../containers/contact-edit/actions/Actions";
import * as ContactAddActions from "../containers/contact-add/actions/Actions";

import {Actions as WebActions} from "../../web/actions/Actions";

import {ValidationError, ContactFields, Amount, RedeemVoucher} from "../../model";
import {Reducer as SummaryReducer} from "../containers/summary/reducers/Reducer";
import {Reducer as PaymentReducer} from "../containers/payment/reducers/Reducer";
import {Reducer as ConcessionReducer} from "../containers/concession/reducers/Reducer";
import {ContactsSchema, ContactsState} from "../../NormalizeSchema";
import {IAction} from "../../actions/IshAction";
import {normalize} from "normalizr";
import {FULFILLED} from "../../common/actions/ActionUtils";
import {CHANGE_TAB} from "../containers/payment/actions/Actions";

// Checking if cart has been modified.
const IsCartModified = (state: boolean = false, action: IAction<boolean>): boolean => {
  switch (action.type) {
    case FULFILLED(WebActions.ADD_CLASS_TO_CART):
    case FULFILLED(WebActions.ADD_PRODUCT_TO_CART):
    case FULFILLED(WebActions.ADD_PROMOTION_TO_CART):
    case FULFILLED(WebActions.ADD_WAITING_COURSE_TO_CART):
    case FULFILLED(WebActions.REMOVE_CLASS_FROM_CART):
    case FULFILLED(WebActions.REMOVE_PRODUCT_FROM_CART):
    case FULFILLED(WebActions.REMOVE_PROMOTION_FROM_CART):
    case FULFILLED(WebActions.REMOVE_WAITING_COURSE_FROM_CART):
      return true;

    case Actions.CHANGE_PHASE:
      return false;

    default:
      return state;
  }
};

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

    case Actions.RESET_CHECKOUT_STATE:
      return Phase.Summary;

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

const FieldsReducer = (state: ContactFieldsState = new ContactFieldsState(), action: IAction<ContactFields>): ContactFieldsState => {
  switch (action.type) {

    case ContactEditActions.SET_FIELDS_TO_STATE:
      return {
        current: action.payload,
        unfilled: state.unfilled.find(fields => fields.contactId === action.payload.contactId)
                  ? state.unfilled
                  : state.unfilled.concat(action.payload),
      };

    case Actions.CHANGE_PHASE:
      switch (action.payload) {
        case Phase.EditContact:
        case Phase.ComplementEditContact:
          return state;
        default:
          return {
            current: null,
            unfilled: state.unfilled.filter(fields => state.current && state.current.contactId !== fields.contactId),
          };
      }

    case ContactEditActions.RESET_FIELDS_STATE:
      return {
        current: null,
        unfilled: state.unfilled.filter(fields => fields.contactId !== state.current.contactId),
      };

    case Actions.RESET_CHECKOUT_STATE:
      return new ContactFieldsState();

    default:
      return state;
  }
};

const ContactsReducer = (
  state: ContactsState = normalize([], ContactsSchema),
  action: IAction<ContactsState & {childId?: string, parentId?: string, childIds?: string[]} & {contactId: string, message?: string}>,
): ContactsState => {

  let ns: ContactsState;

  switch (action.type) {
    case ContactAddActions.ADD_CONTACT_TO_STATE:

      ns = L.cloneDeep(state);
      ns.entities.contact = {...ns.entities.contact, ...action.payload.entities.contact};
      ns.result = Array.from(new Set([...ns.result, ...action.payload.result]));
      return ns;

    case Actions.UPDATE_PARENT_CHILDS_REQUEST:
      ns = L.cloneDeep(state);
      ns.result.map(id => ns.entities.contact[id].parentRequired = false);
      return ns;

    case Actions.UPDATE_PARENT_CHILDS_FULFILLED: {
      const {childIds, parentId} = action.payload;
      ns = L.cloneDeep(state);
      ns.result.map(
        id => childIds.includes(id) ? ns.entities.contact[id].parent = ns.entities.contact[parentId] || null : id,
      );

      return ns;
    }

    case Actions.REMOVE_CONTACT: {
      const {contactId} = action.payload;
      ns = L.cloneDeep(state);

      // remove contact
      ns.result = ns.result.filter(id => id !== contactId);
      delete ns.entities.contact[contactId];

      // check and remove parent
      const isParentFor = Object.values(ns.entities.contact)
        .find(contact => contact.parent && contact.parent.id === contactId);

      if (isParentFor) {
        ns.entities.contact[isParentFor.id].parentRequired = true;
        delete ns.entities.contact[isParentFor.id].parent;
      }

      return ns;
    }

    case Actions.SET_CONTACT_WARNING_MESSAGE: {
      const {contactId, message} = action.payload;
      ns = L.cloneDeep(state);

      ns.entities.contact[contactId].warning = message;
      return ns;
    }


    case Actions.RESET_CHECKOUT_STATE:
      return normalize([], ContactsSchema);

    default:
      return state;
  }
};

const AmountReducer = (state: AmountState = new AmountState(), action: IAction<any>): AmountState => {
  switch (action.type) {

    case Actions.UPDATE_AMOUNT:
      const amount: Amount = action.payload;
      return {...amount, payNowVisibility: state.payNowVisibility};

    case Actions.RESET_CHECKOUT_STATE:
      return new AmountState();

    case Actions.TOGGLE_PAYNOW_VISIBILITY:
      return {
        ...state,
        payNowVisibility: action.payload,
      };

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
    // case Actions.UPDATE_AMOUNT:
    case CHANGE_TAB:
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
      const voucher = {...action.payload, enabled: true};
      return state.filter(v => v.id === voucher.id).length ? state : state.concat(voucher);

    case Actions.SET_REDEEM_VOUCHER_ACTIVITY:
      const {id, enabled} = action.payload;
      return state.map(v => v.id === id ? {...v, enabled} : v);

    case Actions.REMOVE_REDEEM_VOUCHER: {
      const {voucher} = action.payload;
      return state.filter(v => voucher && voucher.id !== v.id );
    }

    case Actions.RESET_CHECKOUT_STATE:
      return [];

    default:
      return state;
  }
};

const ContactAddProcess = (state: any = {}, action: IAction<any>): any => {
  switch (action.type) {

    case Actions.UPDATE_CONTACT_ADD_PROCESS:
      const {contact, type, parent, childId} = action.payload;
      return {
        ...state,
        contact,
        type,
        parent,
        forChild: childId || null,
      };

    default:
      return state;
  }
};

const FetchingReducer = (state: boolean = false, action: IAction<any>): boolean => {
  switch (action.type) {

    case ContactEditActions.OPEN_EDIT_CONTACT_REQUEST:
      return true;

    case FULFILLED(ContactEditActions.OPEN_EDIT_CONTACT_REQUEST):
    case Actions.CHANGE_PHASE:
    case Actions.INIT_REQUEST:
      return false;

    default:
      return state;
  }
};

export const Reducer = combineReducers<CheckoutState>({
  fetching: FetchingReducer,
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
  isCartModified: IsCartModified,
});
