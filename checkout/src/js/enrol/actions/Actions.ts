import {_toRequestType, FULFILLED} from "../../common/actions/ActionUtils";
import {AmountState, Phase} from "../reducers/State";
import * as L from "lodash";
import {AxiosResponse} from "axios";
import {Amount} from "../../model";
import {IAction} from "../../actions/IshAction";
import {GABuilder} from "../../services/GoogleAnalyticsService";
import { RedeemVoucherProduct } from '../../model/checkout/RedeemVoucherProduct';

// initialize checkout application
export const SHOW_MESSAGES: string = "checkout/messages/show";
export const SHOW_MESSAGES_REQUEST: string = _toRequestType(SHOW_MESSAGES);

export const INIT_REQUEST: string = "checkout/init/request";

// change current phase action
export const CHANGE_PHASE: string = "checkout/phase/change";

export const SET_PAYER_TO_STATE: string = "checkout/set/payer/to/state";

export const GET_AMOUNT: string = "checkout/get/amount";
export const UPDATE_AMOUNT: string = "checkout/update/amount";
export const UPDATE_PAYNOW: string = "checkout/update/paynow";

export const SET_NEW_CONTACT_FLAG = "checkout/set/new/contact/flag";

export const RESET_CHECKOUT_STATE = "checkout/reset/state";

export const FINISH_CHECKOUT_PROCESS = "checkout/finish/process";

export const ADD_CODE: string = "checkout/summary/add/code";
export const ADD_CODE_REQUEST: string = _toRequestType(ADD_CODE);

export const GET_CHECKOUT_MODEL_FROM_BACKEND: string = "checkout/get/model/from/backend";

export const ADD_REDEEM_VOUCHER_TO_STATE: string = "checkout/add/redeemVoucher";
export const ADD_REDEEM_VOUCHER_PRODUCTS_TO_STATE: string = "checkout/add/redeemVoucherProducts";

export const SET_REDEEM_VOUCHER_ACTIVITY: string = "checkout/set/voucher/activity";
export const TOGGLE_REDEEM_VOUCHER: string = "checkout/toggle/redeemVoucher";
export const REMOVE_REDEEM_VOUCHER: string = "checkout/remove/redeemVoucher";

export const SET_REDEEM_VOUCHER_PRODUCT_ACTIVITY: string = "checkout/set/voucherProduct/activity";
export const TOGGLE_REDEEM_VOUCHER_PRODUCT: string = "checkout/toggle/redeemVoucherProduct";
export const REMOVE_REDEEM_VOUCHER_PRODUCT: string = "checkout/remove/redeemVoucherProduct";

export const UPDATE_CONTACT_ADD_PROCESS: string = "checkout/update/process/contact-add";

export const UPDATE_PARENT_CHILDS_REQUEST: string = _toRequestType("checkout/update/parent/childs");
export const UPDATE_PARENT_CHILDS_FULFILLED: string = FULFILLED(UPDATE_PARENT_CHILDS_REQUEST);

export const TOGGLE_PAYNOW_VISIBILITY: string = "checkout/update/payNow/visibility";

export const EPIC_REMOVE_CONTACT: string = "epic/checkout/remove/contact";
export const REMOVE_CONTACT: string = "checkout/remove/contact";
export const SET_CONTACT_WARNING_MESSAGE: string = "checkout/contact/warning/message";


export const addRedeemVoucherProductsToState = (vouchers: RedeemVoucherProduct[]) => {
  return {
    type: ADD_REDEEM_VOUCHER_PRODUCTS_TO_STATE,
    payload: vouchers,
  };
};

export const addCode = (code: string): { type: string, payload: string } => {
  return {
    type: ADD_CODE_REQUEST,
    payload: code,
  };
};

export const getCheckoutModelFromBackend = (): IAction<any> => {
  return {
    type: GET_CHECKOUT_MODEL_FROM_BACKEND,
  };
};

export const sendInitRequest = (): IAction<any> => {
  return {
    type: INIT_REQUEST,
  };
};

export const setNewContactFlag = (newContact: boolean): { type: string, payload: boolean } => {
  return {
    type: SET_NEW_CONTACT_FLAG,
    payload: newContact,
  };
};

export const showFormValidation = (response: AxiosResponse, form: string): any => {
  return {
    type: SHOW_MESSAGES_REQUEST,
    payload: response,
    meta: {
      form,
    },
  };
};

export const changePhase = (phase: Phase) => {

  if (L.isNil(phase))
    throw new Error();

  return {
    type: CHANGE_PHASE,
    payload: phase,
    meta: {
      analytics: GABuilder.setCheckoutStep(phase),
    },
  };
};

export const getAmount = (): { type: string } => {
  return {
    type: GET_AMOUNT,
  };
};

export const updateAmount = (amount: AmountState): IAction<Amount> => {
  return {
    type: UPDATE_AMOUNT,
    payload: amount,
  };
};

export const updatePayNow = (val: number, validate: boolean): IAction<any> => {
  return {
    type: UPDATE_PAYNOW,
    payload: {val, validate},
  };
};

export const setRedeemVoucherActivity = (id: string, enabled: boolean): IAction<any> => {
  return {
    type: SET_REDEEM_VOUCHER_ACTIVITY,
    payload: {id, enabled},
  };
};


export const setRedeemVoucherProductActivity = (id: string, enabled: boolean): IAction<any> => {
  return {
    type: SET_REDEEM_VOUCHER_PRODUCT_ACTIVITY,
    payload: {id, enabled},
  };
};

export const toggleRedeemVoucherProduct = (voucher, enabled: boolean): IAction<any> => {
  return {
    type: TOGGLE_REDEEM_VOUCHER_PRODUCT,
    payload: {voucher, enabled},
  };
};

export const toggleRedeemVoucher = (voucher, enabled: boolean): IAction<any> => {
  return {
    type: TOGGLE_REDEEM_VOUCHER,
    payload: {voucher, enabled},
  };
};

export const removeRedeemVoucher = (voucher): IAction<any> => {
  return {
    type: REMOVE_REDEEM_VOUCHER,
    payload: {voucher},
  };
};

export const finishCheckoutProcess = (response): IAction<any> => {
  return {
    type: FINISH_CHECKOUT_PROCESS,
    payload: response,
  };
};

export const resetCheckoutState = (): IAction<any> => {
  return {
    type: RESET_CHECKOUT_STATE,
  };
};

export const setPayer = function (id: string): { type: string, payload: string } {
  return {type: SET_PAYER_TO_STATE, payload: id};
};

export const addRedeemVoucherToState = voucher => ({
  type: FULFILLED(ADD_REDEEM_VOUCHER_TO_STATE),
  payload: voucher,
});

export const updateContactAddProcess = (contact, type, childId) => ({
  type: UPDATE_CONTACT_ADD_PROCESS,
  payload: {contact, type, childId},
});

export const updateParentChilds = (parentId: string, childIds: string[]): IAction<any> => {
  return {
    type: UPDATE_PARENT_CHILDS_REQUEST,
    payload: {parentId, childIds},
  };
};

export const togglePayNowVisibility = (val: boolean): IAction<any> => {
  return {
    type: TOGGLE_PAYNOW_VISIBILITY,
    payload: val,
  };
};

export const showSyncErrors = formErrors => {
  return {
    type: SHOW_MESSAGES,
    payload: {formErrors: Object.values(formErrors).map(error => error)},
  };
};

export const epicRemoveContact = contactId => {
  return {
    type: EPIC_REMOVE_CONTACT,
    payload: {contactId},
  };
};

export const removeContact = contactId => {
  return {
    type: REMOVE_CONTACT,
    payload: {contactId},
  };
};

// export const setContactWarningMessage = (contactId, message) => {
//   return {
//     type: SET_CONTACT_WARNING_MESSAGE,
//     payload: {contactId, message},
//   };
// };
