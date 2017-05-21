import * as React from "react";
import * as ReactDOM from "react-dom";

import {Provider} from "react-redux";
import {CreateStore, RestoreState} from "../../../js/CreateStore";
import {MessagesRedux, ProgressRedux} from "../../../js/enrol/containers/Functions";


import "../../../scss/index.scss";

import {amount, contactPropses} from "./SummaryApp.data";
import Summary from "../../../js/enrol/containers/summary/Summary";


const store = CreateStore();
RestoreState(store, () => render());

const onAddContact = () => {
};
const onProceedToPayment = () => {
};


const render = () => ReactDOM.render(
  <Provider store={store}>
    <div id="checkout" className="payments">
      <ProgressRedux/>
      <MessagesRedux/>
      <Summary contacts={[
        contactPropses[0],
        contactPropses[1]
      ]} amount={amount} onAddContact={onAddContact} onProceedToPayment={onProceedToPayment}/>
    </div>

  </Provider>,
  document.getElementById("root")
);
