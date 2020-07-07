import {CreateStore, RestoreState} from "./CreateStore";
import {Bootstrap} from "./common/utils/Bootstrap";
import {configLoader} from "./configLoader";
import {Level, Logger, LogMessage} from "./services/Logger";
import {ConfigConstants} from "./config/ConfigConstants";
import {WindowService} from "./services/WindowService";
import {HTMLMarkers} from "./common/services/HTMLMarker";
import "../scss/index.scss";
import {getPreferences} from "./common/actions/Actions";
import {setCookie} from "./common/utils/Cookie";

const appStart = () => {
  if (Intl) {
    setCookie(
      'clientTimezoneName',
      Intl.DateTimeFormat().resolvedOptions().timeZone,
      {
        samesite: "strict",
        secure: true,
      },
    );
  }

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
  WindowService.initCheckoutApi(store);

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

