import {IshState} from "../services/IshState";
import {combineReducers} from "redux";
import {reducer as formReducer} from 'redux-form'
import {cartReducer} from "./cart";
import popup from "./popup";
import {coursesReducer} from "./courses";
import {productsReducer} from "./products";
import {enrolReducer} from "./enrol";
import {autocompleteReducer} from "./autocomplete";

export const combinedReducers = combineReducers<IshState>({
  cart: cartReducer,
  popup,
  courses: coursesReducer,
  products: productsReducer,
  enrol: enrolReducer,
  form: formReducer,
  autocomplete: autocompleteReducer
});
