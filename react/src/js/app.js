import { createStore, combineReducers, applyMiddleware } from 'redux';
import thunk from 'redux-thunk';
import Bootstrap from './lib/Bootstrap';
import cart from './reducers/cart';
import popup from './reducers/popup';
import Cart from './containers/Cart';
import EnrolButton from './containers/EnrolButton';
import PopupContainer from './containers/PopupContainer';

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
        classId: 'number',
        className: 'string',
        isCanceled: 'boolean',
        isFinished: 'boolean',
        hasAvailableEnrolmentPlaces: 'boolean',
        allowByApplication: 'boolean',
        paymentGatewayEnabled: 'boolean',
        freePlaces: 'number'
    })
    .register('cart', Cart)
    .register('popup', PopupContainer)
    .start();
