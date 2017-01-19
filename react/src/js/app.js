import { createStore, combineReducers, applyMiddleware } from 'redux';
import thunk from 'redux-thunk';
import Bootstrap from './lib/Bootstrap';
import cart from './reducers/cart';
import Cart from './containers/Cart';
import EnrolButton from './containers/EnrolButton';

let store = createStore(
    combineReducers({ cart }),
    applyMiddleware(thunk)
);

let app = new Bootstrap();

app.setStore(store)
    .register('enrol-button', EnrolButton, {
        id: 'number'
    })
    .register('cart', Cart)
    .start();