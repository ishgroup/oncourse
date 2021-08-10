import { combineReducers } from 'redux';
import { State } from '../model/State';
import loginReducer from './loginReducer';
import messageReducer from './messageReducer';
import thirdPartyReducer from './thirdPartyReducer';
import sessionsReducer from './sessionsReducer';

export const combinedReducers = combineReducers<State>({
  login: loginReducer,
  message: messageReducer,
  thirdParty: thirdPartyReducer,
  sessions: sessionsReducer
});
