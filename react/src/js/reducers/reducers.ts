import {IshState} from "../services/IshState";
import {combineReducers} from "redux";
import {reducer as formReducer} from "redux-form";
import {cartReducer} from "../web/reducers/cart";
import popup from "./popup";
import {coursesReducer} from "../web/reducers/courses";
import {productsReducer} from "../web/reducers/products";
import {Reducer as CheckoutReducer} from "../enrol/reducers/Reducer";
import {CheckoutPathReducer} from "../common/reducers/Reducer";

export const combinedReducers = combineReducers<IshState>({
  form: formReducer,
  cart: cartReducer,
  popup,
  courses: coursesReducer,
  products: productsReducer,
  checkout: CheckoutReducer,
  checkoutPath: CheckoutPathReducer
});
