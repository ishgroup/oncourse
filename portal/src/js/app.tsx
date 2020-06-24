import React from "react";
import ReactDOM from "react-dom";
import PaymentForm from "./payment-form/PaymentForm";

const start = (target: any) => {
  ReactDOM.render(
    <div id="test">
      <PaymentForm />
    </div>,
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
observer.observe(document, config);

