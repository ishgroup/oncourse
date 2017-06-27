import React from "react";
import ReactDOM from "react-dom";
import {Provider} from "react-redux";

import {CreateStore, RestoreState} from "../../../js/CreateStore";
import {Messages as Messages, Progress as Progress} from "../../../js/enrol/containers/Functions";

import Concession from "../../../js/enrol/containers/concession/Concession";
import {concessions, contact} from "./Concession.data";

import "react-select/dist/react-select.css";
import "../../../scss/_ReactSelect.scss";

const store = CreateStore();
RestoreState(store, () => render());

const onConcessionSubmit = (data, dispatch, props) => {
};

const render = () => ReactDOM.render(
  <Provider store={store}>
    <div id="checkout" className="col-xs-24 payments">
      <Progress/>
      <Messages/>
      <Concession/>
    </div>
  </Provider>,
  document.getElementById('root'),
);
