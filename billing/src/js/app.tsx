import React from "react";
import ReactDOM from "react-dom";
import { Provider } from "react-redux";
import { CreateStore } from "./redux";
import Billing from "./components/Billing";
import "../scss/billing.scss";

const store = CreateStore();

export const initApp = () => {
  ReactDOM.render(
    <Provider store={store}>
      <React.StrictMode>
        <Billing />
      </React.StrictMode>
    </Provider>,
    document.getElementById("provisioning")
  );
}

initApp()