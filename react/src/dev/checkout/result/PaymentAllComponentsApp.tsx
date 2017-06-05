import React from "react";
import ReactDOM from "react-dom";
import {Provider} from "react-redux";
import {CreateStore, RestoreState} from "../../../js/CreateStore";
import {Progress} from "../../../js/enrol/containers/Functions";
import {Undefined} from "../../../js/enrol/containers/result/components/Undefined";
import {InProgress} from "../../../js/enrol/containers/result/components/InProgress";
import {Failed} from "../../../js/enrol/containers/result/components/Failed";
import {Successful} from "../../../js/enrol/containers/result/components/Successful";

const store = CreateStore();
RestoreState(store, () => render());

const render = () => ReactDOM.render(
  <Provider store={store}>
    <div id="checkout" className="col-xs-24 payments">
      <Progress />
      <InProgress />
      <Progress />
      <Failed/>
      <Progress />
      <Successful refId={"W1367014"}/>
      <Progress />
      <Undefined />
    </div>
  </Provider>,
  document.getElementById('root')
);