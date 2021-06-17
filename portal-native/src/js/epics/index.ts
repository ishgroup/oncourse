import { combineEpics } from 'redux-observable';
import EpicSignIn from './login/EpicSignIn';
import EpicSignUp from './login/EpicSignUp';
import EpicConnect from './login/EpicConnect';

export const EpicRoot = combineEpics(
  EpicSignIn,
  EpicSignUp,
  EpicConnect
);
