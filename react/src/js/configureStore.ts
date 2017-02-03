import {Store} from "react-redux";
import thunk from "redux-thunk";
import {createStore, combineReducers, applyMiddleware, Action} from "redux";
import cart from "./reducers/cart";
import {addClass, addProduct} from "./actions/cart";
import {IshState, Product, Course, PopupState} from "./services/IshState";
import popup from "./reducers/popup";
import {StoreListenerService} from "./services/StoreListenerService";
import {LocalStorageService} from "./services/LocalStorageService";
import {CookieService} from "./services/CookieService";
import Partial = _.Partial;

export function configureStore(): Store<IshState> {
  const store = createStore(
    combineReducers<IshState>({cart, popup}),
    applyMiddleware(thunk)
  );

  const storeListenerService = new StoreListenerService(store);

  storeListenerService.addListener(state => {
    const courses = state.cart.courses
      .map(course => course.id)
      .join("%");

    if (courses) {
      CookieService.set("shortlist", courses);
      LocalStorageService.set("shortlist", state.cart.courses);
    }
  });

  storeListenerService.addListener(state => {
    const products = state.cart.products
      .map(product => product.id)
      .join("%");

    if (products) {
      CookieService.set("productShortList", products);
      LocalStorageService.set("productShortList", state.cart.products);
    }
  });

  preloadedState(store);

  return store;
}

function preloadedState(store: Store<IshState>) {
  const products = LocalStorageService.get<Product[]>("productShortList") || [];
  const courses = LocalStorageService.get<Course[]>("shortlist") || [];

  products.forEach(product => {
    addProduct(product)(store.dispatch);
  });

  courses.forEach(course => {
    addClass(course)(store.dispatch);
  });
}
