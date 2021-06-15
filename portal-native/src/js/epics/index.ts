import { combineEpics } from 'redux-observable';
import EpicSignIn from './login/EpicSignIn';
import EpicSignUp from './login/EpicSignUp';

export const EpicRoot = combineEpics(
  EpicSignIn,
  EpicSignUp
);
