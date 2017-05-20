import * as React from "react";
import * as ReactDOM from "react-dom";

import {Provider} from "react-redux";
import {CreateStore, RestoreState} from "../../../js/CreateStore";
import {MessagesRedux, ProgressRedux} from "../../../js/enrol/containers/Functions";


import "../../../scss/index.scss";
import ContactComp from "../../../js/enrol/containers/summary/components/ContactComp";

import {contact1Props, contact2Props} from "./SummaryApp.data";



const store = CreateStore();
RestoreState(store, () => render());


const render = () => ReactDOM.render(
  <Provider store={store}>
    <div id="checkout" className="payments">
      <ProgressRedux/>
      <MessagesRedux/>
      <ContactComp {...contact1Props}/>
      <ContactComp {...contact2Props}/>
    </div>

  </Provider>,
  document.getElementById("root")
);
