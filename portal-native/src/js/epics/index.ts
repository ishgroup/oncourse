import { combineEpics } from 'redux-observable';
import EpicSignIn from './login/EpicSignIn';
import EpicSignOut from './login/EpicSignOut';
import EpicEmailLogin from './login/EpicEmailLogin';
import EpicGetUserSessions from './session/EpicGetUserSessions';
import EpicGetThirdPartyClientIds from './thirdParty/EpicGetThirdPartyClientIds';

export const EpicRoot = combineEpics(
  EpicSignIn,
  EpicSignOut,
  EpicEmailLogin,
  EpicGetUserSessions,
  EpicGetThirdPartyClientIds
);
