import { combineEpics } from 'redux-observable';
import EpicSignIn from './login/EpicSignIn';
import EpicEmailLogin from './login/EpicEmailLogin';

export const EpicRoot = combineEpics(
  EpicSignIn,
  EpicEmailLogin
);
