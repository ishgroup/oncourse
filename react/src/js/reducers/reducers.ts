import {IshState} from "../services/IshState";
import {combineReducers} from "redux";
import {reducer as formReducer} from "redux-form";
import {cartReducer} from "../web/reducers/cart";
import popup from "./popup";
import {coursesReducer} from "../web/reducers/courses";
import {productsReducer} from "../web/reducers/products";
import {Reducer as EnrolReducer} from "../enrol/reducers/Reducer";
import {autocompleteReducer} from "./autocomplete";

export const combinedReducers = combineReducers<IshState>({
  cart: cartReducer,
  popup,
  courses: coursesReducer,
  products: productsReducer,
  enrol: EnrolReducer,
  form: formReducer,
  autocomplete: autocompleteReducer
});
