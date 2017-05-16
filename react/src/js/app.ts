import {CreateStore} from "./CreateStore";
import {Bootstrap} from "./common/utils/Bootstrap";
import {PublicApi} from "./external/PublicApi";
import {Level, Logger, LogMessage} from "./services/Logger";
import {ConfigConstants} from "./config/ConfigConstants";
import {WindowService} from "./services/WindowService";
import {HTMLMarkers} from "./common/services/HTMLMarker";

// Log application version before start.
Logger.log(new LogMessage(Level.INFO, `Application version: "${ConfigConstants.APP_VERSION}"`));

const store = CreateStore();

WindowService.set("api", new PublicApi(store));

const react = new Bootstrap(store)
  .register(HTMLMarkers.CHECKOUT)
  .register(HTMLMarkers.FEES)
  .register(HTMLMarkers.ENROL_BUTTON)
  .register(HTMLMarkers.BUY_BUTTON)
  .register(HTMLMarkers.CART)
  .register(HTMLMarkers.MODAL)
  .register(HTMLMarkers.POPUP)
  .register(HTMLMarkers.PROMOTIONS)
  .start();

// Set bootstrap function to Ish.react
// We use this in jquery-managed code,
// to get notifications about new react-markers
WindowService.set("react", react);
