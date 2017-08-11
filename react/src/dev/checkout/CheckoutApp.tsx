import * as React from "react";
import * as ReactDOM from "react-dom";

import {Provider} from "react-redux";
import Checkout from "../../js/enrol/containers/Checkout";

import "../../scss/index.scss";

import {MockControl} from "./MockControl";
import {MockConfig} from "../mocks/mocks/MockConfig";

const config = new MockConfig();

config.init((config:MockConfig) => {
  render(config);
});

const render = config => ReactDOM.render(
  <Provider store={config.store}>
    <div>
      <Checkout/>
      <MockControl config={config}/>
    </div>
  </Provider>,
  document.getElementById("root"),
);

