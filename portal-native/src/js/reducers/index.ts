import { combineReducers } from 'redux';
import loginReducer from './loginReducer';
import messageReducer from './messageReducer';
import thirdPartyReducer from './thirdPartyReducer';
import { State } from '../model/State';

export const combinedReducers = combineReducers<State>({
  login: loginReducer,
  message: messageReducer,
  thirdParty: thirdPartyReducer
});
