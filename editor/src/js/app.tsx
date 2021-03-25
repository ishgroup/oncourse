import 'core-js/modules/es6.object.assign';
import * as React from "react";
import * as ReactDOM from "react-dom";
import {Provider} from "react-redux";
import { persistStore } from 'redux-persist';
import {MemoryRouter as Router} from 'react-router-dom';
import {CreateStore} from "./CreateStore";
import {configLoader} from "./configLoader";
import Cms from "./containers/Cms";
import {createRootComponent, loadCmsCss} from "./utils";
import {DefaultConfig} from "./constants/Config";
import "../scss/cms.scss";
import {PersistGate} from "redux-persist/integration/react";

const store = CreateStore();

export const initApp = () => {

  /**
   *  Load CMS config
   *  Create cms root element
   *  Load cms styles
   **/
  configLoader(store);
  createRootComponent();
  loadCmsCss(store.getState().config.cssPath);

  const start = store => {
    ReactDOM.render(
      <Provider store={store}>
        <PersistGate loading={null} persistor={persistStore(store)}>
          <Router>
            <Cms/>
          </Router>
        </PersistGate>
      </Provider>,
      document.getElementById(DefaultConfig.CONTAINER_ID),
    );
  };

  start(store);
};

initApp();
