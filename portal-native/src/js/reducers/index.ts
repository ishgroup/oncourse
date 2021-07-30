import { combineReducers } from 'redux';
import loginReducer from './loginReducer';
import messageReducer from './messageReducer';

export const combinedReducers = combineReducers({
  login: loginReducer,
  message: messageReducer
});
