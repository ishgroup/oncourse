import * as React from "react";
import * as ReactDOM from "react-dom";
import {Provider} from "react-redux";
import Checkout from "../../js/enrol/containers/Checkout";
import {MockControl} from "./MockControl";
import {MockConfig} from "../mocks/mocks/MockConfig";
import {configLoader} from "../../js/configLoader";
import {getPreferences} from "../../js/common/actions/Actions";
import "../../scss/index.scss";
import EnrolButton from "../../js/web/containers/EnrolButton";
require('intersection-observer');

const config = new MockConfig();

config.init((config:MockConfig) => {

  configLoader(config.store);
  config.store.dispatch(getPreferences());         // get global preferences
  render(config);

});

const render = config => ReactDOM.render(
  <Provider store={config.store}>
    <div id="oncourse-checkout">
      <Checkout/>
      <MockControl config={config}/>
      <EnrolButton />
    </div>
  </Provider>,
  document.getElementById("root"),
);

