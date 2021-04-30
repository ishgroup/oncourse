import React from "react";
import {CreateStore, RestoreState} from "./CreateStore";
import {Bootstrap} from "./common/utils/Bootstrap";
import {configLoader} from "./configLoader";
import {Level, Logger, LogMessage} from "./services/Logger";
import {ConfigConstants} from "./config/ConfigConstants";
import {WindowService} from "./services/WindowService";
import {HTMLMarkers} from "./common/services/HTMLMarker";
import {render} from "react-dom";
import {getPreferences} from "./common/actions/Actions";
import {getCookie, setCookie} from "./common/utils/Cookie";
import {v4 as uuid} from "uuid";
import moment from "moment-timezone";
import localforage from "localforage";
import BrowserDetector from "./common/utils/Browser";
import "../scss/index.scss";
import {LegacyModal} from "./web/components/modal/LegacyModal";


// Intersection Observer polyfill
require('intersection-observer');

// NodeList forEach polyfill
if (window["NodeList"] && !NodeList.prototype["forEach"]) {
  NodeList.prototype["forEach"] = Array.prototype.forEach as any;
}

const appStart = () => {
  const isSupportedBrowser = new BrowserDetector().isSupported();

  if (!isSupportedBrowser) {
    const modalBody = document.createElement("div");
    document.body.append(modalBody);

    render(
      <LegacyModal />,
      modalBody
    );

    WindowService.get("modal").openModal({
      animation: "slideUp",
      content: '<div style="\n' +
        '    padding: 40px;\n' +
        '    font-size: 24px;\n' +
        '">\n' +
        '    <p style="\n' +
        '    font-weight: bold;\n' +
        '"> Your current browser is not supported</p>Please ensure you are using Internet Explorer 11 or an up to date version of Chrome, Firefox, Edge, Safari or Opera</div>',
      duration: 600,
      height: 400,
      width: 1000.
    });

    return;
  }


  setCookie(
    'clientTimezoneName',
    moment.tz.guess(true),
    {
      samesite: "strict",
      secure: true,
    },
  );

  const cartId = getCookie("cartId");

  if (!cartId) {
    setCookie(
      'cartId',
      uuid().replace(/-/g,"").substring(0,30)
    );
  }

  localforage.config({
    driver: [localforage.LOCALSTORAGE, localforage.INDEXEDDB, localforage.WEBSQL]
  });

  localforage.ready().catch(e => {
    console.error(e);
  });

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

  // set initial dataLayer
  if (!window['dataLayer']) {
    window['dataLayer'] = [];
  }

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

