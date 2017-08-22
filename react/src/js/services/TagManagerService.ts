import {PaymentStatus} from "../model";

const sendEvent = data => {
  try {
    if (window['dataLayer'] && window['dataLayer'].push) {
      window['dataLayer'].push(data);
    }
  } catch (e) {
    console.log("Unexpected error with google tag manager - " + e);
  }
};

export const checkoutFinishEvent = data => {
  if (data && data.status) {

    if (data.status === PaymentStatus.SUCCESSFUL || PaymentStatus.SUCCESSFUL_BY_PASS) {
      sendEvent({
        eventName: "purchaseComplete",
        cart: data.cart,
      });
    }

  }
};
