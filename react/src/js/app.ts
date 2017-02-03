import React from "react";
import {createStore, combineReducers, applyMiddleware, Store} from "redux";
import thunk from "redux-thunk";
import cart from "./reducers/cart";
import popup from "./reducers/popup";
import Cart from "./containers/Cart";
import EnrolButton from "./containers/EnrolButton";
import BuyButton from "./containers/BuyButton";
import PopupContainer from "./containers/PopupContainer";
import {StoreListenerService} from "./services/StoreListenerService";
import {CookieService} from "./services/CookieService";
import {IshState} from "./services/IshState";
import {Bootstrap} from "./lib/Bootstrap";

const store = createStore(
  combineReducers<IshState>({cart, popup}),
  applyMiddleware(thunk)
);

const storeListenerService = new StoreListenerService(store);

storeListenerService.addListener(state => {
  const courses = state.cart.courses
    .map(course => course.id)
    .join("%");

  const products = state.cart.courses
    .map(course => course.id)
    .join("%");

  CookieService.set("shortlist", courses);
  CookieService.set("productShortList", products);
});

new Bootstrap(store)
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
  .register('popup', PopupContainer)
  .start();
