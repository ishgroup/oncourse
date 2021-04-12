import 'core-js/modules/es6.object.assign';
import * as React from "react";
import * as ReactDOM from "react-dom";
import {Provider} from "react-redux";
import { persistStore } from 'redux-persist';
import {MemoryRouter as Router} from 'react-router-dom';
import {PersistGate} from "redux-persist/integration/react";
import {CreateStore} from "./CreateStore";
import {configLoader} from "./configLoader";
import Cms from "./containers/Cms";
import {createRootComponent, loadCmsCss} from "./utils";
import {DefaultConfig} from "./constants/Config";
import "../scss/cms.scss";
import StylesProviderCustom from "./styles/StylesProviderCustom";
// import "./CMSCustom";

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
  // const root = document.getElementById(DefaultConfig.CONTAINER_ID);
  // root.attachShadow({ mode: "open" });

  const start = store => {
    ReactDOM.render(
      <Provider store={store}>
        <PersistGate loading={null} persistor={persistStore(store)}>
          {/*{console.log(123, <cms-custom/>)}*/}
          {/*<cms-custom/>*/}
          <StylesProviderCustom>
            <Router>
              <Cms/>
            </Router>
          </StylesProviderCustom>
        </PersistGate>
      </Provider>,
      document.getElementById(DefaultConfig.CONTAINER_ID),
      // root.shadowRoot,
    );
  };

  // const start = store => {
  //   ReactDOM.render(
  //     <Provider store={store}>
  //       <PersistGate loading={null} persistor={persistStore(store)}>
  //         <StylesProviderCustom>
  //           <Router>
  //             <Cms/>
  //           </Router>
  //         </StylesProviderCustom>
  //       </PersistGate>
  //     </Provider>,
  //     document.getElementById(DefaultConfig.CONTAINER_ID),
  //   );
  // };

  start(store);
};

initApp();
