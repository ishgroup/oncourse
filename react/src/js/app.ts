import {configureStore} from "./configureStore";
import Cart from "./containers/Cart";
import EnrolButton from "./containers/EnrolButton";
import {BuyButton} from "./components/buyButton/BuyButton";
import PopupContainer from "./containers/PopupContainer";
import {Bootstrap} from "./lib/Bootstrap";
import {PublicApi} from "./external/PublicApi";

const store = configureStore();

const w = window as any;
// Init Ish namespace in window
w.Ish = w.Ish || {};
// Set Api to public Api
w.Ish.api = new PublicApi(store);

// Set bootstrap function to Ish.react
// We use this in jquery-managed code,
// to get notifications about new react-markers
w.Ish.react = new Bootstrap(store)
  .register('enrol-button', EnrolButton, {
    /**
     * ToDo discuss with Andrey properties for enrol button.
     * Now it almost looks like properties in tapestry template
     */
    id: 'number',
    name: 'string',
    uniqueIdentifier: 'string',
    isCanceled: 'boolean',
    isFinished: 'boolean',
    hasAvailableEnrolmentPlaces: 'boolean',
    allowByApplication: 'boolean',
    paymentGatewayEnabled: 'boolean',
    freePlaces: 'number'
  })
  .register('buy-button', BuyButton, {
    id: 'number',
    name: 'string',
    uniqueIdentifier: 'string',
    paymentGatewayEnabled: 'boolean',
    canBuy: 'boolean'
  })
  .register('cart', Cart)
  .register('popup', PopupContainer)
  .start();
