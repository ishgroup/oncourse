import {configureStore} from "./configureStore";
import {Bootstrap} from "./lib/Bootstrap";
import {PublicApi} from "./external/PublicApi";
import {LogMessage, Level, Logger} from "./services/Logger";
import {ConfigConstants} from "./config/ConfigConstants";
import {Root} from "./containers/Root";
import {WindowService} from "./services/WindowService";
import {CartRoot} from "./containers/CartRoot";
import {Fees} from "./containers/Fees";
import {defaultAxios} from "./services/DefaultHttpClient";
import BuyButton from "./containers/BuyButton";
import EnrolButton from "./containers/EnrolButton";
import PopupContainer from "./containers/PopupContainer";
import Promotions from "./containers/Promotions";

// Log application version before start.
Logger.log(new LogMessage(Level.INFO, `Application version: "${ConfigConstants.APP_VERSION}"`));

const store = configureStore();

WindowService.set("api", new PublicApi(store));

const react = new Bootstrap(store)
  .register("enrol", Root, {})
  .register("fees", Fees, {id: "string"})
  .register("enrol-button", EnrolButton, {id: "string"})
  .register("buy-button", BuyButton, {id: "string"})
  .register("cart", CartRoot, {})
  .register("popup", PopupContainer, {})
  .register("promotions", Promotions, {})
  .start();

// Set bootstrap function to Ish.react
// We use this in jquery-managed code,
// to get notifications about new react-markers
WindowService.set("react", react);
