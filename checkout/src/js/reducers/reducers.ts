import { combineReducers } from 'redux';
import { reducer as formReducer } from 'redux-form';
import { IshState } from '../services/IshState';
import { cartReducer } from '../web/reducers/cart';
import popup from './popup';
import { coursesReducer, waitingCoursesReducer, inactiveCoursesReducer } from '../web/reducers/courses';
import { productsReducer } from '../web/reducers/products';
import { Reducer as CheckoutReducer } from '../enrol/reducers/Reducer';
import { configReducer } from '../common/reducers/Reducer';
import { preferencesReducer } from '../common/reducers/Preferences';
import { suggestionsReducer } from '../web/reducers/suggestions';

export const combinedReducers = combineReducers<IshState>({
  popup,
  form: formReducer,
  cart: cartReducer,
  courses: coursesReducer,
  products: productsReducer,
  waitingCourses: waitingCoursesReducer,
  inactiveCourses: inactiveCoursesReducer,
  checkout: CheckoutReducer,
  config: configReducer,
  preferences: preferencesReducer,
  suggestions: suggestionsReducer
});
