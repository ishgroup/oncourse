import React from "react";
import ReactDOM from "react-dom";
import {Provider} from "react-redux";
import {Messages, Progress} from "../../../js/enrol/containers/Functions";
import {Payment} from "../../../js/enrol/containers/payment/Payment";
import {addContact} from "../../../js/enrol/containers/contact-add/actions/Actions";
import {MockConfig} from "../../mocks/mocks/MockConfig";
import {updateAmount} from "../../../js/enrol/actions/Actions";
import * as MockFunctions from "../../mocks/mocks/MockFunctions";
import "../../scss/index.scss";

const config: MockConfig = new MockConfig();

config.init((config: MockConfig) => {
  config.store.dispatch(addContact(config.db.getContactByIndex(0)));
  config.store.dispatch(updateAmount(MockFunctions.mockAmount()));
  render(config);
});


const render = (config: MockConfig) => ReactDOM.render(
  <Provider store={config.store}>
    <div id="checkout" className="col-xs-24 payments">
      <Progress/>
      <Messages/>
      <Payment/>
    </div>
  </Provider>,
  document.getElementById('root')
);
