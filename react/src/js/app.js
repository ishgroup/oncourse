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
        id: 'number'
    })
    .register('cart', Cart)
    .register('popup', PopupContainer)
    .start();