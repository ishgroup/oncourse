import React from "react";
import ReactDOM from "react-dom";
import Billing from "./components/Billing";
import "../scss/billing.scss";

export const initApp = () => {
  ReactDOM.render(
    <React.StrictMode>
      <Billing />
      <p>Hello React</p>
    </React.StrictMode>,
    document.getElementById("root")
  );
}

initApp()