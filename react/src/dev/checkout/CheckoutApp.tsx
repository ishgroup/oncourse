import * as React from "react";
import * as ReactDOM from "react-dom";

import {Provider} from "react-redux";
import {CreateStore, RestoreState} from "../../js/CreateStore";
import Checkout from "../../js/enrol/containers/Checkout";
import {Injector} from "../../js/injector";

import "../../scss/index.scss";
import * as MockInjector from "../mocks/MockInjector";
import {MockControl} from "./MockControl";


const store = CreateStore();
RestoreState(store, () => render());

const injector = Injector.of();
MockInjector.init(injector);


const render = () => ReactDOM.render(
  <Provider store={store}>
    <div>
      <Checkout/>
      <MockControl store={store} injector={injector}/>
    </div>
  </Provider>,
  document.getElementById("root")
);
