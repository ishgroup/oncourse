import React from "react";
import ReactDOM from "react-dom";
import Billing from "./components/Billing";
import "../scss/billing.scss";

export const initApp = () => {
  ReactDOM.render(
    <React.StrictMode>
      <Billing />
    </React.StrictMode>,
    document.getElementById("billing")
  );
}

initApp()