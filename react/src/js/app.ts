import {configureStore} from "./configureStore";
import {Bootstrap} from "./lib/Bootstrap";
import {PublicApi} from "./external/PublicApi";
import {LogMessage, Level, Logger} from "./services/Logger";
import {ConfigConstants} from "./config/ConfigConstants";
import {WindowService} from "./services/WindowService";
import {MarkerComponents} from "./MarkerComponents";

// Log application version before start.
Logger.log(new LogMessage(Level.INFO, `Application version: "${ConfigConstants.APP_VERSION}"`));

const store = configureStore();

WindowService.set("api", new PublicApi(store));

const react = new Bootstrap(store)
  .register(MarkerComponents.ENROL)
  .register(MarkerComponents.FEES)
  .register(MarkerComponents.ENROL_BUTTON)
  .register(MarkerComponents.BUY_BUTTON)
  .register(MarkerComponents.CART)
  .register(MarkerComponents.MODAL)
  .register(MarkerComponents.POPUP)
  .register(MarkerComponents.PROMOTIONS)
  .start();

// Set bootstrap function to Ish.react
// We use this in jquery-managed code,
// to get notifications about new react-markers
WindowService.set("react", react);
