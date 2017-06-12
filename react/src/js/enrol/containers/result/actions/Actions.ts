import {IAction} from "../../../../actions/IshAction";
export const TRY_ANOTHER_CARD = "checkout/result/try/another/card";
export const CANCEL_CHECKOUT_PROCESS = "checkout/result/cancel/checkout/process";


export const tryAnotherCard = (): IAction<any> => {
  return {
    type: TRY_ANOTHER_CARD,
  }
};


export const cancelCheckoutProcess = (): IAction<any> => {
  return {
    type: CANCEL_CHECKOUT_PROCESS,
  }
};