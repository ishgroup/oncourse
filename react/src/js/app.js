import { createStore, combineReducers, applyMiddleware } from 'redux';
import thunk from 'redux-thunk';
import Bootstrap from './lib/Bootstrap';
import cart from './reducers/cart';
import popup from './reducers/popup';
import Cart from './containers/Cart';
import EnrolButton from './containers/EnrolButton';
import BuyButton from './containers/BuyButton';
import PopupContainer from './containers/PopupContainer';

// ToDo initialize global variable in webpack
window.jQuery = $;

let store = createStore(
    combineReducers({ cart, popup }),
    applyMiddleware(thunk)
);

let app = new Bootstrap();

app.setStore(store)
    .register('enrol-button', EnrolButton, {
        /**
         * ToDo discuss with Andrey properties for enrol button.
         * Now it almost looks like properties in tapestry template
         */
        id: 'number',
        name: 'string',
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
        paymentGatewayEnabled: 'boolean',
        canBuy: 'boolean'
    })
    .register('cart', Cart)
    .register('popup', PopupContainer);

$(document).ready(() => {
    app.start();
});