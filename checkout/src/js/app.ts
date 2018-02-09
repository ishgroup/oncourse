if (!global['_babelPolyfill']) {
  require("babel-polyfill");
}

import {CreateStore, RestoreState} from "./CreateStore";
import {Bootstrap} from "./common/utils/Bootstrap";
import {PublicApi} from "./external/PublicApi";
import {configLoader} from "./configLoader";
import {Level, Logger, LogMessage} from "./services/Logger";
import {ConfigConstants} from "./config/ConfigConstants";
import {WindowService} from "./services/WindowService";
import {HTMLMarkers} from "./common/services/HTMLMarker";

import "../scss/index.scss";
import {getPreferences} from "./common/actions/Actions";

const appStart = () => {
  const store = CreateStore();

  configLoader(store);

  RestoreState(store, () => {
    start(store);
  });
};

appStart();

// Log application version before start.
Logger.log(new LogMessage(Level.INFO, `Application version: "${ConfigConstants.APP_VERSION}"`));

const start = store => {
  WindowService.set("api", new PublicApi(store));

  // get global preferences
  store.dispatch(getPreferences());

  const react = new Bootstrap(store)
    .register(HTMLMarkers.CHECKOUT)
    .register(HTMLMarkers.FEES)
    .register(HTMLMarkers.ENROL_BUTTON)
    .register(HTMLMarkers.BUY_BUTTON)
    .register(HTMLMarkers.JOIN_BUTTON)
    .register(HTMLMarkers.CART)
    .register(HTMLMarkers.MODAL)
    .register(HTMLMarkers.POPUP)
    .register(HTMLMarkers.PROMOTIONS)
    .start();

// Set bootstrap function to Ish.react
// We use this in jquery-managed code,
// to get notifications about new react-markers
  WindowService.set("react", react);
};

