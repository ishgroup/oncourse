import {IshState} from "../services/IshState";
import {combineReducers} from "redux";
import {cartReducer} from "./cart";
import popup from "./popup";
import {coursesReducer} from "./courses";
import {productsReducer} from "./products";

export const combinedReducers = combineReducers<IshState>({
  cart: cartReducer,
  popup,
  courses: coursesReducer,
  products: productsReducer
});
