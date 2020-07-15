import React from "react";
import ReactDOM from "react-dom";
import PaymentForm from "./payment-form/PaymentForm";

const query = window.location.href;
const sessionIdMatch = query.match(/sessionId=([^&#?]+)/);
const statusMatch = query.match(/paymentStatus=([^&#?]+)/);
let isRedirect = false;

if (sessionIdMatch && sessionIdMatch[1] && statusMatch && statusMatch[1]) {
  isRedirect = true;
  window.parent.postMessage(
    {
      payment: {
        sessionId: sessionIdMatch[1],
        status: statusMatch[1],
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

