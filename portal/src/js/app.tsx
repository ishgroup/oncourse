import React from "react";
import ReactDOM from "react-dom";
import PaymentForm from "./payment-form/PaymentForm";

const query = new URLSearchParams(window.location.search);
const sessionId = query.get("sessionId");
const status = query.get("paymentStatus");
let isRedirect = false;

if (sessionId && status) {
  isRedirect = true;
  window.parent.postMessage(
    {
      payment: {
        sessionId,
        status,
      },
    },
    "*",
  );
}

const start = (target: any) => {
  ReactDOM.render(
    <PaymentForm />,
    target,
  );
};

const config = {attributes: true, childList: true, subtree: true};

const callback = function (mutationsList: any, observer: any) {
  for (const mutation of mutationsList) {
    if (mutation.type === 'childList') {

      const target = document.getElementById("react-payment-form");

      if (target) {
        start(target);
        observer.disconnect();
      }
    }
  }
};

const observer = new MutationObserver(callback);

if (!isRedirect) {
  observer.observe(document, config);
}

