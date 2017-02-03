import {configureStore} from "./configureStore";
import Cart from "./containers/Cart";
import EnrolButton from "./containers/EnrolButton";
import BuyButton from "./containers/BuyButton";
import PopupContainer from "./containers/PopupContainer";
import {Bootstrap} from "./lib/Bootstrap";

const store = configureStore();

new Bootstrap(store)
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
